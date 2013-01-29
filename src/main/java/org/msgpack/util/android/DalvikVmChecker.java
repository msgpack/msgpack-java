package org.msgpack.util.android;

public final class DalvikVmChecker {
    private static final boolean isDalvikVm;
    static {
        boolean isIt = false;
        try {
            isIt = System.getProperty("java.vm.name").equals("Dalvik");
        } finally {
            isDalvikVm = isIt;
        }
    }
    
    public static boolean isDalvikVm() {
        return isDalvikVm;
    }
}
