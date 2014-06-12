package org.msgpack.testclasses;

import org.junit.Ignore;
import org.msgpack.annotation.*;

@Ignore @MessagePackBeans
public class IndexedFieldsBeanClassNotNullable {

    public String f5;

    public String f4;

    public String f3;

    public String f2;

    public String f1;

    @Index(0) @NotNullable
    public String getF5() {
        return f5;
    }

    @NotNullable
    public void setF5(String f5) {
        this.f5 = f5;
    }

    @Index(4) @NotNullable
    public String getF4() {
        return f4;
    }

    @NotNullable
    public void setF4(String f4) {
        this.f4 = f4;
    }

    @NotNullable
    public String getF3() {
        return f3;
    }

    @Index(1) @NotNullable
    public void setF3(String f3) {
        this.f3 = f3;
    }

    @NotNullable
    public String getF2() {
        return f2;
    }

    @Index(3) @NotNullable
    public void setF2(String f2) {
        this.f2 = f2;
    }

    @Index(2) @NotNullable
    public String getF1() {
        return f1;
    }

    @NotNullable
    public void setF1(String f1) {
        this.f1 = f1;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof IndexedFieldsBeanClassNotNullable)) {
            return false;
        }
        IndexedFieldsBeanClassNotNullable that = (IndexedFieldsBeanClassNotNullable) o;
        if (f5 == null) {
            if (that.f5 != null) {
                return false;
            }
        }
        if (that.f5 != null && ! f5.equals(that.f5)) {
            return false;
        }
        if (f4 == null) {
            if (that.f4 != null) {
                return false;
            }
        }
        if (that.f4 != null && ! f4.equals(that.f4)) {
            return false;
        }
        if (f3 == null) {
            if (that.f3 != null) {
                return false;
            }
        }
        if (that.f3 != null && ! f3.equals(that.f3)) {
            return false;
        }
        if (f2 == null) {
            if (that.f2 != null) {
                return false;
            }
        }
        if (that.f2 != null && ! f2.equals(that.f2)) {
            return false;
        }
        if (f1 == null) {
            if (that.f1 != null) {
                return false;
            }
        }
        if (that.f1 != null && ! f1.equals(that.f1)) {
            return false;
        }

        return true;
    }
}
