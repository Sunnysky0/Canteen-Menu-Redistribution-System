package cn.sunnysky.user.security;

import cn.sunnysky.user.security.crypto.digests.MD4Digest;

public class SecurityManager {

    public static String hashNTLM(String original) {
        try {
            if (original == null) {
                original = "";
            }
            MD4Digest md4 = new MD4Digest();
            int len = original.length();
            byte[] pwdBytes = new byte[len * 2];

            for (int i = 0; i < len; i++) {
                char ch = original.charAt(i);
                pwdBytes[i * 2] = (byte) ch;
                pwdBytes[i * 2 + 1] = (byte) ((ch >> 8) & 0xFF);
            }

            md4.update(pwdBytes, 0, pwdBytes.length);
            byte[] encPwd = new byte[16];
            md4.doFinal(encPwd, 0);

            return toHexString(encPwd).substring(0, 32).toUpperCase();

        } catch (Throwable nsae) {

            System.out.println(nsae.getMessage());
            return null;

        }
    }
    public static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String toHexString(byte[] b) {
        return toHexString(b, 0, b.length);
    }

    public static String toHexString(byte[] b, int off, int len) {
        char[] buf = new char[len * 2];

        for (int i = 0, j = 0, k; i < len;) {
            k = b[off + i++];
            buf[j++] = HEX_DIGITS[(k >>> 4) & 0x0F];
            buf[j++] = HEX_DIGITS[k & 0x0F];
        }

        return new String(buf);
    }

}
