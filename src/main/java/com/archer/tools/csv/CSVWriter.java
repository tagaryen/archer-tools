package com.archer.tools.csv;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * High performance csv writer with low memory footprint.
 * <p>
 * Usage:
 * <pre>
 * try(CSVWriter w = CSVWriter.to("a.csv")) {
 *     w.writeHeader("id", "name");
 *     w.writeRow(1, "tom");
 * }
 * </pre>
 * Data is encoded and flushed through an internal 64KB buffer,
 * so heap usage stays constant no matter how large the output is.
 */
public class CSVWriter implements Closeable, Flushable {

    private static final int BUF_SIZE = 1 << 16;

    private final OutputStream out;

    private Charset charset = StandardCharsets.UTF_8;
    private boolean utf8 = true;
    private char separator = ',';
    private char quoteChar = '"';
    private byte[] lineBreak = {'\n'};
    private boolean alwaysQuote = false;
    private boolean bom = false;

    private byte[] buf = new byte[BUF_SIZE];
    private int pos = 0;
    private boolean wrote = false;

    private CSVWriter(OutputStream out) {
        this.out = out;
    }

    public static CSVWriter to(Path path) throws IOException {
        return new CSVWriter(Files.newOutputStream(path));
    }

    public static CSVWriter to(Path path, boolean append) throws IOException {
        if(append) {
            return new CSVWriter(Files.newOutputStream(path, StandardOpenOption.CREATE, StandardOpenOption.APPEND));
        }
        return new CSVWriter(Files.newOutputStream(path));
    }

    public static CSVWriter to(String path) throws IOException {
        return to(Paths.get(path));
    }

    public static CSVWriter to(String path, boolean append) throws IOException {
        return to(Paths.get(path), append);
    }

    public static CSVWriter to(OutputStream out) {
        return new CSVWriter(out);
    }

    public CSVWriter charset(Charset charset) {
        checkConfig();
        this.charset = charset;
        this.utf8 = StandardCharsets.UTF_8.equals(charset);
        return this;
    }

    public CSVWriter separator(char separator) {
        checkConfig();
        checkChar(separator);
        this.separator = separator;
        return this;
    }

    public CSVWriter quoteChar(char quoteChar) {
        checkConfig();
        checkChar(quoteChar);
        this.quoteChar = quoteChar;
        return this;
    }

    public CSVWriter lineBreak(String lineBreak) {
        checkConfig();
        if(lineBreak == null || lineBreak.isEmpty()) {
            throw new IllegalArgumentException("lineBreak can not be empty.");
        }
        this.lineBreak = lineBreak.getBytes(StandardCharsets.US_ASCII);
        return this;
    }

    public CSVWriter alwaysQuote(boolean alwaysQuote) {
        checkConfig();
        this.alwaysQuote = alwaysQuote;
        return this;
    }

    /** write a utf8 BOM header before the first row, helps excel to open utf8 csv correctly */
    public CSVWriter utf8Bom(boolean bom) {
        checkConfig();
        this.bom = bom;
        return this;
    }

    private void checkConfig() {
        if(wrote) {
            throw new IllegalStateException("config must be set before writing any row.");
        }
    }

    private static void checkChar(char c) {
        if(c >= 128 || c == '\r' || c == '\n') {
            throw new IllegalArgumentException("separator/quoteChar must be an ascii char except \\r and \\n.");
        }
    }

    public CSVWriter writeHeader(String... header) throws IOException {
        return writeRow((Object[]) header);
    }

    public CSVWriter writeRow(List<?> cells) throws IOException {
        if(cells == null) {
            return writeRow((Object[]) null);
        }
        return writeRow(cells.toArray());
    }

    public CSVWriter writeRow(Object... cells) throws IOException {
        firstWrite();
        if(cells == null || cells.length == 0) {
            writeBytes(lineBreak);
            return this;
        }
        if(cells.length == 1 && (cells[0] == null || "".equals(cells[0]))) {
            // a lone empty cell must be quoted, otherwise it is written as
            // a blank line which can not be told apart from an empty line
            writeByte((byte) quoteChar);
            writeByte((byte) quoteChar);
            writeBytes(lineBreak);
            return this;
        }
        if(utf8) {
            for(int i = 0; i < cells.length; i++) {
                if(i > 0) {
                    writeByte((byte) separator);
                }
                writeCellUtf8(cells[i]);
            }
            writeBytes(lineBreak);
        } else {
            writeRowEncoded(cells);
        }
        return this;
    }

    private void firstWrite() throws IOException {
        if(!wrote) {
            wrote = true;
            if(bom) {
                writeByte((byte) 0xEF);
                writeByte((byte) 0xBB);
                writeByte((byte) 0xBF);
            }
        }
    }

    private void writeCellUtf8(Object cell) throws IOException {
        String s = cell == null ? "" : (cell instanceof String ? (String) cell : String.valueOf(cell));
        if(s.isEmpty()) {
            if(alwaysQuote) {
                ensure(2);
                buf[pos++] = (byte) quoteChar;
                buf[pos++] = (byte) quoteChar;
            }
            return ;
        }
        boolean q = alwaysQuote || needQuote(s);
        ensure(s.length() * 4 + 2);
        if(q) {
            buf[pos++] = (byte) quoteChar;
        }
        int qi = q ? s.indexOf(quoteChar) : -1;
        if(qi < 0) {
            pos = encodeUtf8(s, 0, s.length(), buf, pos);
        } else {
            int start = 0;
            while(qi >= 0) {
                pos = encodeUtf8(s, start, qi, buf, pos);
                buf[pos++] = (byte) quoteChar;
                buf[pos++] = (byte) quoteChar;
                start = qi + 1;
                qi = s.indexOf(quoteChar, start);
            }
            pos = encodeUtf8(s, start, s.length(), buf, pos);
        }
        if(q) {
            buf[pos++] = (byte) quoteChar;
        }
    }

    /** slow path for non utf8 charsets, build the whole row then encode at once */
    private void writeRowEncoded(Object[] cells) throws IOException {
        StringBuilder sb = new StringBuilder(cells.length << 4);
        for(int i = 0; i < cells.length; i++) {
            if(i > 0) {
                sb.append(separator);
            }
            String s = cells[i] == null ? "" : String.valueOf(cells[i]);
            boolean q = alwaysQuote || needQuote(s);
            if(q) {
                sb.append(quoteChar);
                for(int j = 0; j < s.length(); j++) {
                    char c = s.charAt(j);
                    if(c == quoteChar) {
                        sb.append(quoteChar);
                    }
                    sb.append(c);
                }
                sb.append(quoteChar);
            } else {
                sb.append(s);
            }
        }
        for(byte b : lineBreak) {
            sb.append((char) b);
        }
        byte[] b = sb.toString().getBytes(charset);
        writeBytes(b);
    }

    private boolean needQuote(String s) {
        for(int i = 0, n = s.length(); i < n; i++) {
            char c = s.charAt(i);
            if(c == separator || c == quoteChar || c == '\r' || c == '\n') {
                return true;
            }
        }
        return false;
    }

    private void ensure(int n) throws IOException {
        if(pos + n > buf.length) {
            out.write(buf, 0, pos);
            pos = 0;
            if(n > buf.length) {
                buf = new byte[Math.max(buf.length << 1, n)];
            }
        }
    }

    private void writeByte(byte b) throws IOException {
        if(pos >= buf.length) {
            out.write(buf, 0, pos);
            pos = 0;
        }
        buf[pos++] = b;
    }

    private void writeBytes(byte[] b) throws IOException {
        ensure(b.length);
        System.arraycopy(b, 0, buf, pos, b.length);
        pos += b.length;
    }

    private static int encodeUtf8(String s, int from, int to, byte[] b, int p) {
        for(int i = from; i < to; i++) {
            char c = s.charAt(i);
            if(c < 0x80) {
                b[p++] = (byte) c;
            } else if(c < 0x800) {
                b[p++] = (byte) (0xC0 | (c >>> 6));
                b[p++] = (byte) (0x80 | (c & 0x3F));
            } else if(c < 0xD800 || c > 0xDFFF) {
                b[p++] = (byte) (0xE0 | (c >>> 12));
                b[p++] = (byte) (0x80 | ((c >>> 6) & 0x3F));
                b[p++] = (byte) (0x80 | (c & 0x3F));
            } else {
                int cp;
                if(c <= 0xDBFF && i + 1 < to && s.charAt(i + 1) >= 0xDC00 && s.charAt(i + 1) <= 0xDFFF) {
                    cp = s.codePointAt(i);
                    i++;
                } else {
                    cp = 0xFFFD;
                }
                b[p++] = (byte) (0xF0 | (cp >>> 18));
                b[p++] = (byte) (0x80 | ((cp >>> 12) & 0x3F));
                b[p++] = (byte) (0x80 | ((cp >>> 6) & 0x3F));
                b[p++] = (byte) (0x80 | (cp & 0x3F));
            }
        }
        return p;
    }

    @Override
    public void flush() throws IOException {
        if(pos > 0) {
            out.write(buf, 0, pos);
            pos = 0;
        }
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if(pos > 0) {
            out.write(buf, 0, pos);
            pos = 0;
        }
        out.close();
    }

    /** write all rows into a byte array with default configs */
    public static byte[] writeAll(List<? extends List<?>> rows) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(1 << 20);
        CSVWriter w = to(bos);
        try {
            if(rows != null) {
                for(List<?> row : rows) {
                    w.writeRow(row);
                }
            }
            w.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    /** write all rows into a csv file with default configs */
    public static void saveAsCsvFile(List<? extends List<?>> rows, String path) throws IOException {
        try(CSVWriter w = to(path)) {
            if(rows != null) {
                for(List<?> row : rows) {
                    w.writeRow(row);
                }
            }
        }
    }
}
