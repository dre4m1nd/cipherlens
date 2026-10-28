package com.dre4m1nd.cipherlens.common.util;

public final class UserHolderUtil {
    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();


    public static void set(Long userId) {
        USER_HOLDER.set(userId);
    }
    public static Long get() {
        return USER_HOLDER.get();
    }
    public static void remove() {
        USER_HOLDER.remove();
    }
}
