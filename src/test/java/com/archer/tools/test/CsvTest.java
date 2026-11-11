package com.archer.tools.test;

import com.archer.tools.csv.CSVReader;
import com.archer.tools.csv.CSVWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CsvTest {

    static final int COLS = 12;

    /** deterministic cell value, so the read side can verify byte by byte */
    static Object cell(int r, int c) {
        switch (c % 6) {
            case 0: return "id_" + r + "_" + c;
            case 1: return r * 31L + c;
            case 2: return "姓名" + r + "，测试,含逗号";
            case 3: return "quoted \"value\" " + r;
            case 4: return "line1\nline2_" + r;
            default:
                if(r % 7 == 0) {
                    return null;
                }
                return 3.1415926d * c + r;
        }
    }

    static String cellStr(int r, int c) {
        Object o = cell(r, c);
        return o == null ? "" : String.valueOf(o);
    }

    static long usedMem() {
        Runtime rt = Runtime.getRuntime();
        System.gc();
        return (rt.totalMemory() - rt.freeMemory()) >> 20;
    }

    /** correctness: random tricky values, writeAll -> readAll round trip */
    static void testRoundTrip() {
        Random rand = new Random(42);
        String[] pool = {
                "", "a", "plain", "with,comma", "with\"quote", "both\",x",
                "line\nbreak", "carriage\r\nreturn", "中文内容", "emoji😀",
                "\"\"", ",start", "end,", " trail ", "\"whole\""
        };
        int rows = 200000;
        List<List<Object>> data = new java.util.ArrayList<>(rows);
        for(int i = 0; i < rows; i++) {
            int cols = 1 + rand.nextInt(8);
            List<Object> row = new java.util.ArrayList<>(cols);
            for(int j = 0; j < cols; j++) {
                int t = rand.nextInt(10);
                if(t < 6) {
                    row.add(pool[rand.nextInt(pool.length)]);
                } else if(t < 8) {
                    row.add(rand.nextLong());
                } else if(t < 9) {
                    row.add(rand.nextDouble());
                } else {
                    row.add(null);
                }
            }
            data.add(row);
        }
        byte[] bytes = CSVWriter.writeAll(data);
        List<List<String>> back = CSVReader.readAll(bytes);
        if(back.size() != data.size()) {
            throw new RuntimeException("row count mismatch: " + back.size() + " != " + data.size());
        }
        for(int i = 0; i < data.size(); i++) {
            List<Object> src = data.get(i);
            List<String> dst = back.get(i);
            if(dst.size() != src.size()) {
                throw new RuntimeException("col count mismatch at row " + i);
            }
            for(int j = 0; j < src.size(); j++) {
                String expect = src.get(j) == null ? "" : String.valueOf(src.get(j));
                if(!expect.equals(dst.get(j))) {
                    throw new RuntimeException("value mismatch at [" + i + "," + j + "]: <" + expect + "> != <" + dst.get(j) + ">");
                }
            }
        }
        System.out.println("[PASS] round trip: " + rows + " rows, bytes=" + bytes.length);
    }

    /** generate a big csv file with streaming writer */
    static Path testBigFile(int rows) throws IOException {
        Path file = Paths.get("e:/csv/test-large.csv");
        Files.createDirectories(file.getParent());

        long mem0 = usedMem();
        long t0 = System.nanoTime();
        try(CSVWriter w = CSVWriter.to(file)) {
            for(int r = 0; r < rows; r++) {
                Object[] row = new Object[COLS];
                for(int c = 0; c < COLS; c++) {
                    row[c] = cell(r, c);
                }
                w.writeRow(row);
            }
        }
        long t1 = System.nanoTime();
        long mem1 = usedMem();
        long size = Files.size(file);
        System.out.printf("[WRITE] rows=%d size=%.1fMB time=%.2fs speed=%.1fMB/s %.0frows/s heapDelta=%dMB%n",
                rows, size / 1048576.0, (t1 - t0) / 1e9, size / 1048576.0 / ((t1 - t0) / 1e9),
                rows / ((t1 - t0) / 1e9), mem1 - mem0);
        return file;
    }

    /** streaming read + full content verification with constant memory */
    static void testStreamRead(Path file, int rows) throws IOException {
        long mem0 = usedMem();
        long t0 = System.nanoTime();
        long[] count = {0};
        long size = Files.size(file);
        try(CSVReader r = CSVReader.open(file)) {
            String[] row;
            while((row = r.readRow()) != null) {
                int ri = (int) count[0];
                if(row.length != COLS) {
                    throw new RuntimeException("col count mismatch at row " + ri + ": " + row.length);
                }
                for(int c = 0; c < COLS; c++) {
                    if(!cellStr(ri, c).equals(row[c])) {
                        throw new RuntimeException("value mismatch at [" + ri + "," + c + "]: <" + cellStr(ri, c) + "> != <" + row[c] + ">");
                    }
                }
                count[0]++;
            }
        }
        long t1 = System.nanoTime();
        long mem1 = usedMem();
        if(count[0] != rows) {
            throw new RuntimeException("row count mismatch: " + count[0] + " != " + rows);
        }
        System.out.printf("[READ-STREAM] rows=%d time=%.2fs speed=%.1fMB/s %.0frows/s heapDelta=%dMB%n",
                count[0], (t1 - t0) / 1e9, size / 1048576.0 / ((t1 - t0) / 1e9),
                count[0] / ((t1 - t0) / 1e9), mem1 - mem0);
        System.out.println("[PASS] stream read verified all values");
    }

    /** load-all read for comparison, uses a smaller file to fit the heap */
    static void testReadAll() throws IOException {
        Path file = Paths.get("e:/csv/test-small.csv");
        Files.createDirectories(file.getParent());
        int rows = 100000;
        try(CSVWriter w = CSVWriter.to(file)) {
            for(int r = 0; r < rows; r++) {
                Object[] row = new Object[COLS];
                for(int c = 0; c < COLS; c++) {
                    row[c] = cell(r, c);
                }
                w.writeRow(row);
            }
        }
        long mem0 = usedMem();
        long t0 = System.nanoTime();
        List<List<String>> data = CSVReader.readAll(file);
        long t1 = System.nanoTime();
        long mem1 = usedMem();
        if(data.size() != rows) {
            throw new RuntimeException("readAll row count mismatch: " + data.size());
        }
        System.out.printf("[READ-ALL] rows=%d time=%.2fs speed=%.1fMB/s heapDelta=%dMB%n",
                data.size(), (t1 - t0) / 1e9,
                Files.size(file) / 1048576.0 / ((t1 - t0) / 1e9), mem1 - mem0);
    }

    /** custom configs: semicolon separator + CRLF + BOM */
    static void testConfigs() throws IOException {
        Path file = Paths.get("e:/csv/test-config.csv");
        Files.createDirectories(file.getParent());
        List<List<Object>> data = Arrays.asList(
                Arrays.asList("a", "b,c", "d\"e", null),
                Arrays.asList("中文", "x\ny", 1.5, 100)
        );
        try(CSVWriter w = CSVWriter.to(file).separator(';').lineBreak("\r\n").utf8Bom(true)) {
            for(List<Object> row : data) {
                w.writeRow(row);
            }
        }
        try(CSVReader r = CSVReader.open(file).separator(';')) {
            String[] row;
            int i = 0;
            while((row = r.readRow()) != null) {
                List<Object> src = data.get(i++);
                for(int j = 0; j < src.size(); j++) {
                    String expect = src.get(j) == null ? "" : String.valueOf(src.get(j));
                    if(!expect.equals(row[j])) {
                        throw new RuntimeException("config test mismatch at [" + i + "," + j + "]");
                    }
                }
            }
            if(i != data.size()) {
                throw new RuntimeException("config test row count mismatch");
            }
        }
        byte[] head = new byte[3];
        try(java.io.InputStream in = Files.newInputStream(file)) {
            int n = in.read(head);
            if(n != 3) {
                throw new RuntimeException("config test bom read failed");
            }
        }
        if((head[0] & 0xFF) != 0xEF || (head[1] & 0xFF) != 0xBB || (head[2] & 0xFF) != 0xBF) {
            throw new RuntimeException("bom missing");
        }
        System.out.println("[PASS] custom separator + CRLF + BOM");
    }

    public static void main(String[] args) throws IOException {
        testRoundTrip();
        testConfigs();

        int rows = args.length > 0 ? Integer.parseInt(args[0]) : 3000000;
        Path big = testBigFile(rows);
        testStreamRead(big, rows);
        testReadAll();
    }
}
