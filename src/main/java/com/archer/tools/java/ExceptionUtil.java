package com.archer.tools.java;

public class ExceptionUtil {
	
    private static final char COLON = ':';
    private static final char SPACE = ' ';
    private static final char SPLIT = ';';
    private static final int stackDepth = 10;
    private static final String more = "...";

    public static String formatException(Throwable ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int depth = Math.min(stackDepth, stackTrace.length);
        StringBuilder sb = new StringBuilder(ex.getClass().getName()).append(COLON).append(SPACE);
        sb.append(ex.getLocalizedMessage()).append(SPLIT).append(SPACE);
        for(int i= 0; i < depth; i++) {
            StackTraceElement el = stackTrace[i];
            sb.append(el.getClassName()).append(COLON)
                    .append(el.getLineNumber()).append(SPLIT).append(SPACE);
        }
        if(stackDepth < stackTrace.length) {
            sb.append(more).append(SPLIT).append(SPACE);
        }
        if(ex.getCause() != null) {
            sb.append(formatException(ex.getCause()));
        }
        return sb.toString();
    }
}
