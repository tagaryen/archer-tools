package com.archer.tools.excel;

import com.archer.tools.java.ArcherList;
import com.archer.tools.java.Base64Util;
import com.archer.tools.java.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class FastXlsxWriter {
    private static final String rowFormat1 = "<row r=\"";
    private static final String rowFormat2 = "\" spans=\"1:";
    private static final String rowFormat3 = "\" x14ac:dyDescent=\"0.25\">";
    private static final String rowFormat4 = "</row>";
    private static final String sheetTail = "<phoneticPr fontId=\"1\" type=\"noConversion\"/>";

    private static void convertToXlsxData(List<SimpleSheet> srcSheets, OutputStream os) throws IOException {
        List<SimpleSheet> sheets = new ArcherList<>();
        if(srcSheets.isEmpty()) {
            SimpleSheet sheet = new SimpleSheet("sheet1");
            sheet.rows(Collections.singletonList(new ArcherList<>()));
            sheets.add(sheet);
        } else {
            sheets.addAll(srcSheets);
        }
        byte[] data = Base64Util.decodeFromString(Constant.basicData), buf;
        int off = 0, read = 0;
        String content = null;
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
            List<String> vc = new ArcherList<>(256);
            int sheetIdx = 0, sharedCount = 0;
            StringBuilder refSb = new StringBuilder(sheets.size() * 128);
            StringBuilder wbSb = new StringBuilder(sheets.size() * 128);
            StringBuilder appSb = new StringBuilder(sheets.size() * 128);
            for(SimpleSheet sheet: sheets) {
                ++sheetIdx;
                refSb.append("<Relationship Id=\"rId"+(2+sheetIdx)+"\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet" + sheetIdx + ".xml\"/>");
                wbSb.append("<sheet name=\""+sheet.getName()+"\" sheetId=\"1\" r:id=\"rId"+(2+sheetIdx)+"\"/>");
                appSb.append("<vt:vector size=\"1\" baseType=\"lpstr\"><vt:lpstr>"+sheet.getName()+"</vt:lpstr></vt:vector>");

                zipOut.putNextEntry(new ZipEntry("xl/worksheets/sheet" + sheetIdx + ".xml"));
                StringBuilder sb = new StringBuilder(10 * 1024 * 1024);
                int r = 1, c = 0, t = 0, maxRow = sheet.rows().size(), maxCell = 1;
                for(List<String> row: sheet.rows()) {
                    if(maxCell < row.size()) {
                        maxCell = row.size();
                    }
                }
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
                        if(StringUtil.isEmpty(cell) || StringUtil.isNumber(cell)) {
                            sb.append("<c r=\""+cellName+"\"><v>"+cell+"</v></c>");
                        } else {
                            sharedCount++;
                            sb.append(getCellStr(cell, cellName + r, vc));
                        }
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
                maxCellName = (char)('A' + (maxCell % 26 - 1)) + maxCellName;
                content = Constant.sheetFormat.replace(Constant.scale, "A1:" + maxCellName + maxRow);
                if(sb.length() == 0) {
                    content = content.replace(Constant.sheetData, "<sheetData/>");
                } else {
                    content = content.replace(Constant.sheetData, "<sheetData>" + sb.toString() +  "</sheetData>");
                    content = content.replace(Constant.phonetic, sheetTail);
                }
                zipOut.write(content.getBytes(StandardCharsets.UTF_8));
                zipOut.closeEntry();
            }

            if(!vc.isEmpty()) {
                refSb.append("<Relationship Id=\"rId"+(3+sheetIdx)+"\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\" Target=\"sharedStrings.xml\"/>");

                zipOut.putNextEntry(new ZipEntry("xl/sharedStrings.xml"));
                StringBuilder strobe = new StringBuilder(1024 * 1024);
                for(String v: vc) {
                    strobe.append("<si><t>").append(v).append("</t><phoneticPr fontId=\"1\" type=\"noConversion\"/></si>");
                }
                content = Constant.sharedStr.replace(Constant.strings, strobe.toString());
                content = content.replace(Constant.count, sharedCount+"");
                content = content.replace(Constant.uniqueCount, vc.size()+"");
                zipOut.write(content.getBytes(StandardCharsets.UTF_8));
                zipOut.closeEntry();
            }

            zipOut.putNextEntry(new ZipEntry("xl/_refs/workbook.xml.rels"));
            content = Constant.ref.replace(Constant.refs, refSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            zipOut.putNextEntry(new ZipEntry("xl/workbook.xml"));
            content = Constant.workBook.replace(Constant.sheets, wbSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();


            zipOut.putNextEntry(new ZipEntry("docProps/app.xml"));
            content = Constant.app.replace(Constant.appSheets, appSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();
        }
    }

    private static String getCellStr(String v, String cellName, List<String> vc) {
        int idx = 0;
        for(String c: vc) {
            if(c.equals(v)) {
                break;
            }
            idx++;
        }
        if(idx >= vc.size()) {
            vc.add(v);
        }
        return "<c r=\""+cellName+"\" t=\"s\"><v>"+idx+"</v></c>";
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
