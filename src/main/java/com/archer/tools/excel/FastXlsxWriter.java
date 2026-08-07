package com.archer.tools.excel;

import com.archer.tools.java.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FastXlsxWriter {
    private static final String rowFormat1 = "<row r=\"";
    private static final String rowFormat2 = "\" spans=\"1:";
    private static final String rowFormat3 = "\" x14ac:dyDescent=\"0.25\">";
    private static final String rowFormat4 = "</row>";

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
                zipOut.putNextEntry(new ZipEntry(entry.getName()));
                zipOut.write(buf, 0, off);
                zipOut.closeEntry();
            }
            List<String> vc = new ArcherList<>(256);
            int sheetIdx = 0, sharedCount = 0;
            StringBuilder refSb = new StringBuilder(sheets.size() * 128);
            StringBuilder wbSb = new StringBuilder(sheets.size() * 128);
            StringBuilder appSb = new StringBuilder(sheets.size() * 128);
            StringBuilder xmlsSb = new StringBuilder(sheets.size() * 256);
            for(SimpleSheet sheet: sheets) {
                if(ContainerUtil.isEmpty(sheet.rows())) {
                    sheet.rows(Collections.singletonList(new ArcherList<>()));
                }
                ++sheetIdx;
                refSb.append("<Relationship Id=\"rId"+(2+sheetIdx)+"\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet" + sheetIdx + ".xml\"/>");
                wbSb.append("<sheet name=\""+sheet.getName()+"\" sheetId=\""+sheetIdx+"\" r:id=\"rId"+(2+sheetIdx)+"\"/>");
                appSb.append("<vt:lpstr>"+sheet.getName()+"</vt:lpstr>");
                xmlsSb.append("<Override PartName=\"/xl/worksheets/sheet"+ sheetIdx +".xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>");

                zipOut.putNextEntry(new ZipEntry("xl/worksheets/sheet" + sheetIdx + ".xml"));
                Pair<Integer, String> maxCells = getMaxCellAndName(sheet);
                int r = 1, c = 0, maxRow = sheet.rows().size(), maxCell = maxCells.getFirst();
                String cellName = "", maxCellName = maxCells.getSecond();
                StringBuilder sb = new StringBuilder(sheet.rows().size() * maxCell * 48);
                for(List<String> row: sheet.rows()) {
                    c = 0;
                    if(ContainerUtil.isEmpty(row)) {
                        continue ;
                    }
                    sb.append(rowFormat1).append(r).append(rowFormat2).append(maxCell).append(rowFormat3);
                    for(String cell: row) {
                        cellName = getCellName(c);
                        if(StringUtil.isEmpty(cell) || StringUtil.isNumber(cell)) {
                            sb.append("<c r=\""+cellName+r+"\"><v>"+cell+"</v></c>");
                        } else {
                            sharedCount++;
                            sb.append(getCellIndexStr(cell, cellName + r, vc));
                        }
                        c++;
                    }
                    sb.append(rowFormat4);
                    r++;
                }
                if(sb.length() == 0) {
                    content = Constant.sheetFormat.replace(Constant.scale, "A1");
                    content = content.replace(Constant.sheetData, "<sheetData/>");
                } else {
                    content = Constant.sheetFormat.replace(Constant.scale, "A1:" + maxCellName + maxRow);
                    content = content.replace(Constant.sheetData, "<sheetData>" + sb.toString() +  "</sheetData>");
                }
                zipOut.write(content.getBytes(StandardCharsets.UTF_8));
                zipOut.closeEntry();
            }

            if(!vc.isEmpty()) {
                refSb.append("<Relationship Id=\"rId"+(3+sheetIdx)+"\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/sharedStrings\" Target=\"sharedStrings.xml\"/>");
                xmlsSb.append("<Override PartName=\"/xl/sharedStrings.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sharedStrings+xml\"/>");

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

            zipOut.putNextEntry(new ZipEntry("xl/_rels/workbook.xml.rels"));
            content = Constant.ref.replace(Constant.refs, refSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            zipOut.putNextEntry(new ZipEntry("xl/workbook.xml"));
            content = Constant.workBook.replace(Constant.sheets, wbSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            zipOut.putNextEntry(new ZipEntry("docProps/app.xml"));
            content = Constant.app.replace(Constant.appSheets, appSb.toString());
            content = content.replace(Constant.sheetSize, sheets.size()+"");
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();

            zipOut.putNextEntry(new ZipEntry("[Content_Types].xml"));
            content = Constant.contentTypeXml.replace(Constant.xmls, xmlsSb.toString());
            zipOut.write(content.getBytes(StandardCharsets.UTF_8));
            zipOut.closeEntry();
        }
    }

    private static Pair<Integer, String> getMaxCellAndName(SimpleSheet sheet) {
        int maxCell = 1;
        for(List<String> row: sheet.rows())
            if(maxCell < row.size())
                maxCell = row.size();
        String maxCellName = "";
        while((maxCell / 26) > 0) {
            maxCellName = (char)('A' + (maxCell % 26)) + maxCellName;
            maxCell = maxCell / 26 - 1;
        }
        maxCellName = (char)('A' + (maxCell % 26 - 1)) + maxCellName;
        return new Pair<>(maxCell, maxCellName);
    }

    private static String getCellName(int t) {
        String cellName = "";
        while((t / 26) > 0) {
            cellName = (char)('A' + (t % 26)) + cellName;
            t = t / 26 - 1;
        }
        cellName = (char)('A' + (t % 26)) + cellName;
        return cellName;
    }

    private static String getCellIndexStr(String v, String cellName, List<String> vc) {
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

    public static byte[] saveAsXlsxBytes(List<SimpleSheet> sheets) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        convertToXlsxData(sheets, out);
        return out.toByteArray();
    }

    public static void saveAsXlsxFile(List<SimpleSheet> sheets, String dstPath) throws IOException {
        try(FileOutputStream fos = new FileOutputStream(dstPath)) {
            convertToXlsxData(sheets, fos);
        }
    }
}
