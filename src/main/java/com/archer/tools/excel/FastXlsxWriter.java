package com.archer.tools.excel;

import com.archer.tools.java.Base64Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

final class FastXlsxWriter {
    private static final String rowFormat1 = "<row r=\"";
    private static final String rowFormat2 = "\" spans=\"1:";
    private static final String rowFormat3 = "\" x14ac:dyDescent=\"0.25\">";
    private static final String rowFormat4 = "</row>";
    private static final String cellFormat1 = "<c r=\"";
    private static final String cellFormat2 = "\"><v>";
    private static final String cellFormat3 = "</v></c>";

    private static void convertToXlsxData(List<SimpleSheet> sheets, OutputStream os) throws IOException {
        byte[] data = Base64Util.decodeFromString(Constant.basicData), buf;
        int off = 0, read = 0;
        try(ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(data)); ZipOutputStream zipOut = new ZipOutputStream(os)) {
            ZipEntry entry;
            while((entry = zipIn.getNextEntry()) != null) {
                zipOut.putNextEntry(entry);
                off = read = 0;
                buf = new byte[1024];
                while((read = zipIn.read(buf, off, buf.length - off)) >= 0) {
                    off += read;
                    if(off >= buf.length) {
                        byte[] na = new byte[buf.length * 2];
                        System.arraycopy(buf, 0, na, 0, buf.length);
                        buf = na;
                    }
                }
                zipOut.write(buf, 0, off);
                zipOut.closeEntry();
            }
            int sheetIdx = 0;
            for(SimpleSheet sheet: sheets) {
                zipOut.putNextEntry(new ZipEntry("xl/worksheets/sheet" + (++sheetIdx) + ".xml"));
                StringBuilder sb = new StringBuilder(10 * 1024 * 1024);
                int r = 1, c = 0, t = 0, maxRow = sheet.rows().size(), maxCell = 1;
                for(List<String> row: sheet.rows()) {
                    if(maxCell < row.size()) {
                        maxCell = row.size();
                    }
                }
                System.out.println("; maxCell = " + maxCell);
                String cellName = "";
                for(List<String> row: sheet.rows()) {
                    c = 0;
                    sb.append(rowFormat1).append(r).append(rowFormat2).append(maxCell).append(rowFormat3);
                    for(String cell: row) {
                        t = c;
                        cellName = "";
                        while((t / 26) > 0) {
                            cellName = (char)('A' + (t % 26)) + cellName;
                            t = t / 26 - 1;
                        }
                        cellName = (char)('A' + (t % 26)) + cellName;
                        sb.append(cellFormat1).append(cellName + r).append(cellFormat2).append(cell).append(cellFormat3);
                        c++;
                    }
                    sb.append(rowFormat4);
                    r++;
                }
                String maxCellName = "";
                while((maxCell / 26) > 0) {
                    maxCellName = (char)('A' + (maxCell % 26)) + maxCellName;
                    maxCell = maxCell / 26 - 1;
                }
                maxCellName = (char)('A' + (maxCell % 26)) + maxCellName;
                String content = Constant.sheetFormat.replace(Constant.scale, "A1:" + maxCellName + maxRow);
                if(sb.length() == 0) {
                    content = content.replace(Constant.sheetData, "<sheetData/>");
                } else {
                    content = content.replace(Constant.sheetData, "<sheetData>" + sb.toString() +  "</sheetData>");
                }
                zipOut.write(content.getBytes(StandardCharsets.UTF_8));
                zipOut.closeEntry();
            }
        }
    }

    public static ByteArrayOutputStream saveAsXlsxStream(List<SimpleSheet> sheets) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        convertToXlsxData(sheets, out);
        return out;
    }

    public static void saveAsXlsxFile(List<SimpleSheet> sheets, String dstPath) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(dstPath)) {
            convertToXlsxData(sheets, fos);
        }
    }
}
