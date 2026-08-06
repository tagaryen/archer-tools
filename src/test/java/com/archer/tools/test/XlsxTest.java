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

    public static void readXlsx() {
        List<SimpleSheet> datas = null;
        try {
            datas = FastXlsxReader.read("E:/projects/excel-test/smalldata.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        SimpleSheet sheet = datas.get(0);
        System.out.println(sheet.rows().get(4).get(5));
    }


    public static void writeBasic() {
        try {
            byte[] data = Files.readAllBytes(Paths.get("E:/projects/excel-test/basic.zip"));
            String base64Str = Base64Util.encodeToString(data);
            String c = "";
            int i = 256;
            for(; i < base64Str.length(); i += 256) {
                c += "\"" + base64Str.substring(i - 256, i) + "\" + \n";
            }
            c += "\"" + base64Str.substring(i - 256) + "\" \n";
            Files.write(Paths.get("e:/basic.txt"), c.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void write() {
        List<List<String>> rows = Arrays.asList(
                Arrays.asList("i", "3", "你"),
                Arrays.asList("j", "4", "哈"),
                Arrays.asList("i", "5", "阿萨"),
                Arrays.asList("j", "6", "发"),
                Arrays.asList("i", "7", "去")
        );
        SimpleSheet sheet = new SimpleSheet("徐熠");
        sheet.rows(rows);
        try {
            FastXlsxWriter.saveAsXlsxFile(Arrays.asList(sheet), "e:/test-w.xlsx");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
//        readXlsx();
        write();
//        writeBasic();
    }
}
