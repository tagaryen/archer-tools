package com.archer.tools.csv;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CSVReader implements Closeable {

    @FunctionalInterface
    public interface RowCallback {
        void onRow(long rowNum, String[] row);
    }

    private static final int BUF_SIZE = 1 << 16;
    private static final int ROW = 1;
    private static final int EMPTY = 2;
    private static final int EOF = 0;

    private final InputStream in;
    private final byte[] buf = new byte[BUF_SIZE];
    private int pos = 0;
    private int limit = 0;

    private Charset charset = StandardCharsets.UTF_8;
    private char separator = ',';
    private char quoteChar = '"';
    private boolean skipEmptyLines = true;

    /** buffer for fields containing escaped quotes or spanning buffer refills */
    private byte[] field = new byte[128];
    private int fieldLen = 0;
    /** reusable column slots */
    private String[] cols = new String[16];
    private int colCount = 0;

    private boolean started = false;

    private CSVReader(InputStream in) {
        this.in = in;
    }

    public static CSVReader open(Path path) throws IOException {
        return new CSVReader(Files.newInputStream(path));
    }

    public static CSVReader open(String path) throws IOException {
        return open(Paths.get(path));
    }

    public static CSVReader open(InputStream in) {
        return new CSVReader(in);
    }

    public CSVReader charset(Charset charset) {
        checkConfig();
        this.charset = charset;
        return this;
    }

    public CSVReader separator(char separator) {
        checkConfig();
        checkChar(separator);
        this.separator = separator;
        return this;
    }

    public CSVReader quoteChar(char quoteChar) {
        checkConfig();
        checkChar(quoteChar);
        this.quoteChar = quoteChar;
        return this;
    }

    public CSVReader skipEmptyLines(boolean skipEmptyLines) {
        checkConfig();
        this.skipEmptyLines = skipEmptyLines;
        return this;
    }

    private void checkConfig() {
        if(started) {
            throw new IllegalStateException("config must be set before reading any row.");
        }
    }

    private static void checkChar(char c) {
        if(c >= 128 || c == '\r' || c == '\n') {
            throw new IllegalArgumentException("separator/quoteChar must be an ascii char except \\r and \\n.");
        }
    }

    /**
     * Read next row, returns null on end of input.
     * The returned array is freshly allocated for each row.
     */
    public String[] readRow() throws IOException {
        if(!started) {
            started = true;
            checkBom();
        }
        for(;;) {
            int r = parseRow();
            if(r == ROW) {
                return Arrays.copyOf(cols, colCount);
            }
            if(r == EOF) {
                return null;
            }
        }
    }

    /** streaming read, rows are passed to the callback one by one with constant memory */
    public void forEach(RowCallback callback) throws IOException {
        String[] row;
        long n = 0;
        while((row = readRow()) != null) {
            callback.onRow(++n, row);
        }
    }

    public static void forEach(Path path, RowCallback callback) throws IOException {
        try(CSVReader r = open(path)) {
            r.forEach(callback);
        }
    }

    public static void forEach(String path, RowCallback callback) throws IOException {
        forEach(Paths.get(path), callback);
    }

    public static void forEach(InputStream in, RowCallback callback) throws IOException {
        try(CSVReader r = open(in)) {
            r.forEach(callback);
        }
    }

    /** read all rows, prefer {@link #forEach} for large files to keep memory low */
    public static List<List<String>> readAll(byte[] bytes) {
        try {
            return readAll(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<List<String>> readAll(String path) throws IOException {
        return readAll(Paths.get(path));
    }

    public static List<List<String>> readAll(Path path) throws IOException {
        try(InputStream in = Files.newInputStream(path)) {
            return readAll(in);
        }
    }

    public static List<List<String>> readAll(InputStream in) throws IOException {
        List<List<String>> rows = new ArrayList<>(1024);
        try(CSVReader r = new CSVReader(in)) {
            String[] row;
            while((row = r.readRow()) != null) {
                rows.add(Arrays.asList(row));
            }
        }
        return rows;
    }

    private int parseRow() throws IOException {
        colCount = 0;
        fieldLen = 0;
        int st = 0; // 0 field start, 1 in plain field, 2 in quotes, 3 quote closed
        boolean any = false;
        byte sep = (byte) separator;
        byte quote = (byte) quoteChar;
        for(;;) {
            if(pos >= limit) {
                if(fill()) {
                    continue ;
                }
                if(st == 0 && !any && colCount == 0) {
                    return EOF;
                }
                endField();
                return ROW;
            }
            switch (st) {
                case 0: {
                    byte c = buf[pos];
                    if(c == quote) {
                        st = 2;
                        any = true;
                        pos++;
                    } else if(c == sep) {
                        pos++;
                        endField();
                        any = true;
                    } else if(c == '\n') {
                        pos++;
                        if(any || !skipEmptyLines) {
                            endField();
                            return ROW;
                        }
                        return EMPTY;
                    } else if(c == '\r') {
                        pos++;
                        consumeLf();
                        if(any || !skipEmptyLines) {
                            endField();
                            return ROW;
                        }
                        return EMPTY;
                    } else {
                        st = 1;
                        any = true;
                    }
                    break;
                }
                case 1: {
                    int k = pos;
                    while(k < limit) {
                        byte c = buf[k];
                        if(c == sep || c == '\n' || c == '\r') {
                            break;
                        }
                        k++;
                    }
                    if(k < limit) {
                        emitField(pos, k);
                        byte c = buf[k];
                        pos = k + 1;
                        if(c == sep) {
                            st = 0;
                        } else {
                            if(c == '\r') {
                                consumeLf();
                            }
                            return ROW;
                        }
                    } else {
                        appendRange(pos, limit);
                        pos = limit;
                    }
                    break;
                }
                case 2: {
                    int k = pos;
                    while(k < limit && buf[k] != quote) {
                        k++;
                    }
                    appendRange(pos, k);
                    if(k < limit) {
                        pos = k + 1;
                        st = 3;
                    } else {
                        pos = limit;
                    }
                    break;
                }
                case 3: {
                    byte c = buf[pos++];
                    if(c == quote) {
                        appendByte(quote);
                        st = 2;
                    } else if(c == sep) {
                        endField();
                        st = 0;
                    } else if(c == '\n') {
                        endField();
                        return ROW;
                    } else if(c == '\r') {
                        consumeLf();
                        endField();
                        return ROW;
                    } else {
                        // lenient: treat chars after closing quote as plain field content
                        appendByte(c);
                        st = 1;
                    }
                    break;
                }
            }
        }
    }

    /** field [from, to) is complete, build the string straight from buf when possible */
    private void emitField(int from, int to) {
        if(colCount == cols.length) {
            cols = Arrays.copyOf(cols, cols.length << 1);
        }
        if(fieldLen == 0) {
            cols[colCount++] = new String(buf, from, to - from, charset);
        } else {
            appendRange(from, to);
            cols[colCount++] = new String(field, 0, fieldLen, charset);
            fieldLen = 0;
        }
    }

    private void endField() {
        if(colCount == cols.length) {
            cols = Arrays.copyOf(cols, cols.length << 1);
        }
        cols[colCount++] = fieldLen == 0 ? "" : new String(field, 0, fieldLen, charset);
        fieldLen = 0;
    }

    private void appendByte(byte b) {
        if(fieldLen == field.length) {
            field = Arrays.copyOf(field, field.length << 1);
        }
        field[fieldLen++] = b;
    }

    private void appendRange(int from, int to) {
        int n = to - from;
        if(n <= 0) {
            return ;
        }
        if(fieldLen + n > field.length) {
            int cap = field.length << 1;
            while(cap < fieldLen + n) {
                cap <<= 1;
            }
            field = Arrays.copyOf(field, cap);
        }
        System.arraycopy(buf, from, field, fieldLen, n);
        fieldLen += n;
    }

    private void consumeLf() throws IOException {
        if(pos >= limit && !fill()) {
            return ;
        }
        if(buf[pos] == '\n') {
            pos++;
        }
    }

    private void checkBom() throws IOException {
        if(pos >= limit) {
            fill();
        }
        if(limit - pos >= 3 && (buf[pos] & 0xFF) == 0xEF
                && (buf[pos + 1] & 0xFF) == 0xBB && (buf[pos + 2] & 0xFF) == 0xBF) {
            pos += 3;
        }
    }

    private boolean fill() throws IOException {
        limit = in.read(buf, 0, buf.length);
        pos = 0;
        if(limit <= 0) {
            limit = 0;
            return false;
        }
        return true;
    }

    @Override
    public void close() throws IOException {
        in.close();
    }
}
