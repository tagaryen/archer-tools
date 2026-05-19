package com.archer.tools.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	
    private static final int BUF_SIZE = 1024 * 1024;

    public static void compress(String sourceDir, String zipFilePath) throws FileNotFoundException, IOException {
    	try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            File source = new File(sourceDir);
            addToZip(source, source.getName(), zos);
        }
    }
    
    public static void decompress(String zipFilePath, String outputDir) throws IOException {
        File dest = new File(outputDir);
        if (!dest.exists()) dest.mkdirs();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(dest, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }
    
    public static byte[] compress(byte[] input) {
        Deflater compresser = new Deflater();
        compresser.setInput(input);
        compresser.finish();
        int offset = 0;
        byte[] buf = new byte[BUF_SIZE];
        while(!compresser.finished()) {
            offset += compresser.deflate(buf, offset, buf.length - offset);
            if(offset >= buf.length) {
                byte[] newBuf = new byte[buf.length << 1];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                buf = newBuf;
            }
        }
        compresser.end();
        byte[] output = new byte[offset];
        System.arraycopy(buf, 0, output, 0, offset);
        return output;
    }


    public static byte[] decompress(byte[] input) throws DataFormatException {
        Inflater decompresser = new Inflater();
        decompresser.setInput(input);
        int offset = 0;
        byte[] buf = new byte[BUF_SIZE];
        while(!decompresser.finished()) {
            offset += decompresser.inflate(buf, offset, buf.length - offset);
            if(offset >= buf.length) {
                byte[] newBuf = new byte[buf.length << 1];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                buf = newBuf;
            }
        }
        decompresser.end();
        byte[] output = new byte[offset];
        System.arraycopy(buf, 0, output, 0, offset);
        return output;
    }
    
    
    private static void addToZip(File source, String entryName, ZipOutputStream zos) throws IOException {
        if (source.isDirectory()) {
            if (!entryName.endsWith("/")) entryName += "/";
            zos.putNextEntry(new ZipEntry(entryName));
            zos.closeEntry(); 

            File[] files = source.listFiles();
            if (files != null) {
                for (File child : files) {
                    addToZip(child, entryName + child.getName(), zos);
                }
            }
        } else {
            zos.putNextEntry(new ZipEntry(entryName));
            try (FileInputStream fis = new FileInputStream(source)) {
                byte[] buffer = new byte[8192];
                int len;
                while ((len = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
            }
            zos.closeEntry();
        }
    }
}
