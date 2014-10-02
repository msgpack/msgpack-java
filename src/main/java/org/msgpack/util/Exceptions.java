package org.msgpack.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {
    /**
     * Prints an exception as a {@link String} suitable for logging.
     *
     * @param e the exception to print
     * @return the exception formatted as a string
     */
    public static String getStackTraceAsString(Exception e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
}
