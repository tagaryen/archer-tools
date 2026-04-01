package com.archer.tools.java;

public class ExceptionUtil {
	
    private static final char COLON = ':';
    private static final char DOT = '.';
    private static final String TAB = "    ";
    private static final char SPACE = ' ';
    private static final char LR = '\n';
    private static final int stackDepth = 10;
    private static final String cause = "Caused by: ";

    public static String formatException(Throwable ex) {
        StackTraceElement[] stackTrace = ex.getStackTrace();
        int depth = Math.min(stackDepth, stackTrace.length);
        StringBuilder sb = new StringBuilder(ex.getClass().getName()).append(COLON).append(SPACE);
        sb.append(ex.getLocalizedMessage()).append(LR);
        for(int i= 0; i < depth; i++) {
            StackTraceElement el = stackTrace[i];
            sb.append(TAB).append(el.getClassName())
                    .append(DOT).append(el.getMethodName()).append(COLON)
                    .append(el.getLineNumber()).append(LR);
        }
        if(ex.getCause() != null) {
            sb.append(cause).append(formatException(ex.getCause()));
        }
        return sb.toString();
    }
}
