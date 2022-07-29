package cn.sunnysky.user.security.crypto.utils;

public interface Memoable {

    Memoable copy();

    void reset(Memoable var1);
}
