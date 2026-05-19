package com.archer.tools.java;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class PathUtil {

    public static String getClassPath() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String path = new File(url.getPath()).getAbsolutePath();
        try {
            return URLDecoder.decode(path, StandardCharsets.UTF_8.name()) + File.separator;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getCurrentWorkDir() {
        try {
            return (new File("")).getCanonicalPath() + File.separator;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String getParentDir(String path) {
    	int sep = path.lastIndexOf(File.separator);
    	if(sep < 0) {
    		return getParentDir(getCurrentWorkDir() + path);
    	}
    	if(sep == 0) {
    		return File.separator;
    	}
    	return path.substring(0, sep);
    }
    
    public static String getLastFileName(String path) {
    	int idx = path.lastIndexOf(File.separator);
    	if(idx < 0) {
    		return path;
    	}
    	if(idx >= path.length()) {
    		throw new IllegalArgumentException("Invalid path: " + path);
    	}
    	return path.substring(idx + 1);
    }
    
    public static String getSuffixName(String path) {
    	int pathIdx = path.lastIndexOf(File.separator);
    	int idx = path.indexOf('.', pathIdx+1);
    	if(idx < 0) {
    		return "";
    	}
    	if(idx >= path.length()) {
    		throw new IllegalArgumentException("Invalid path: " + path);
    	}
    	return path.substring(idx);
    }
    
    public static void mkdirs(File f) {
        if(!f.exists()) {
            if(!f.mkdirs()) {
                throw new RuntimeException("Mkdirs error "+f.toString());
            }
        }
    }

    public static boolean deleteDirs(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            if (children == null) {
                return dir.delete();
            }
            for (String child : children) {
                boolean success = deleteDirs(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
