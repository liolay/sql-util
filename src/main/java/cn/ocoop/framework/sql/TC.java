package cn.ocoop.framework.sql;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TC {
    private ThreadLocal<Object> threadLocal = new InheritableThreadLocal<>();

    public void set(Object o) {
        threadLocal.set(o);
    }

    public Object get() {
        return threadLocal.get();
    }

    public void clear() {
        threadLocal.remove();
    }
}
