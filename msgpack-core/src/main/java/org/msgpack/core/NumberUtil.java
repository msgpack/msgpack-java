package org.msgpack.core;

import java.math.BigInteger;

/**
 * Utilities for numbers
 */
public class NumberUtil {

    private static final BigInteger BI_BYTE_MIN = BigInteger.valueOf(Byte.MIN_VALUE);
    private static final BigInteger BI_BYTE_MAX = BigInteger.valueOf(Byte.MAX_VALUE);
    private static final BigInteger BI_SHORT_MIN = BigInteger.valueOf(Short.MIN_VALUE);
    private static final BigInteger BI_SHORT_MAX = BigInteger.valueOf(Short.MAX_VALUE);
    private static final BigInteger BI_INT_MIN = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigInteger BI_INT_MAX = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger BI_LONG_MIN = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger BI_LONG_MAX = BigInteger.valueOf(Long.MAX_VALUE);

    public static class LongUtil {

        public static boolean isValidByte(long v) {
            return Byte.MIN_VALUE <= v && v <= Byte.MAX_VALUE;
        }

        public static boolean isValidByte(BigInteger v) {
            return v.compareTo(BI_BYTE_MIN) >= 0 && v.compareTo(BI_BYTE_MAX) <= 0;
        }

        public static boolean isValidShort(long v) {
            return Short.MIN_VALUE <= v && v <= Short.MAX_VALUE;
        }

        public static boolean isValidShort(BigInteger v) {
            return v.compareTo(BI_SHORT_MIN) >= 0 && v.compareTo(BI_SHORT_MAX) <= 0;
        }

        public static boolean isValidInt(long v) {
            return Integer.MIN_VALUE <= v && v <= Integer.MAX_VALUE;
        }
        public static boolean isValidInt(BigInteger v) {
            return v.compareTo(BI_INT_MIN) >= 0 && v.compareTo(BI_INT_MAX) <= 0;
        }

        public static boolean isValidLong(BigInteger v) {
            return v.compareTo(BI_LONG_MIN) >= 0 && v.compareTo(BI_LONG_MAX) <= 0;
        }
    }




}
