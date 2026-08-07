package com.archer.tools.test;

import com.archer.tools.excel.FastXlsxReader;
import com.archer.tools.excel.FastXlsxWriter;
import com.archer.tools.excel.SimpleSheet;
import com.archer.tools.java.Base64Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class XlsxTest {


    public static void writeBasic() {
        try {
            byte[] data = Files.readAllBytes(Paths.get("E:/excel/basic.zip"));
            String base64Str = Base64Util.encodeToString(data);
            String c = "";
            int i = 256;
            for(; i < base64Str.length(); i += 256) {
                c += "\"" + base64Str.substring(i - 256, i) + "\" + \n";
            }
            c += "\"" + base64Str.substring(i - 256) + "\" \n";
            Files.write(Paths.get("e:/excel/basic.txt"), c.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void write() {
        List<List<String>> rows = Arrays.asList(
                Arrays.asList("i", "3", "你"),
                Arrays.asList("j", "4", null),
                Arrays.asList("i", "5", "阿萨"),
                Arrays.asList("j", null, "发"),
                Arrays.asList("i", "7", "去")
        );
        SimpleSheet sheet = new SimpleSheet("徐熠");
        sheet.rows(rows);
        List<List<String>> rows2 = Arrays.asList(
                Arrays.asList("fuck", "3", "你是大傻逼"),
                Arrays.asList("asshole", "兼任", "哈")
        );
        SimpleSheet sheet2 = new SimpleSheet("sheet页2");
        sheet2.rows(rows2);
        try {
            FastXlsxWriter.saveAsXlsxFile(Arrays.asList(sheet,sheet2), "e:/excel/test-w.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void readXlsx() {
        List<SimpleSheet> datas = null;
        try {
            datas = FastXlsxReader.read("e:/excel/test-w.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(datas.get(0).getName());
        System.out.println(datas.get(1).getName());
        System.out.println(datas.get(1).rows().get(0).get(2));
        try {
            byte[] bs = FastXlsxWriter.saveAsXlsxBytes(datas);
            Files.write(Paths.get("e:/excel/test-w-c.xlsx"), bs);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        write();
        readXlsx();
//        writeBasic();
    }
}
