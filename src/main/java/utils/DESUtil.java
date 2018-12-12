package utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * 采用MD5加密解密
 *
 * @author tfq
 * @datetime 2011-10-13
 */
public class DESUtil {

    private static final String KEY = "kimwaynet";// 密钥
    private final static String DES = "DES";// DES算法名称
    private static final Logger LOGGER = LoggerFactory.getLogger(DESUtil.class);

    //加密
    private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    //解密
    private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance(DES);
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
        return cipher.doFinal(src);
    }

    private static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();

    }

    private static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("length not even");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    //解密
    public static String decode(String src, String key) {
        if (StringUtils.isNotEmpty(key)) {
            key = KEY;
        }
        String decryptStr = "";
        try {
            decryptStr = new String(decrypt(hex2byte(src.getBytes()), key.getBytes()));
        } catch (Exception ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        return decryptStr;
    }

    public static String decode(String src) {
        return decode(src, KEY);
    }

    //加密
    public static String encode(String src, String key) {
        if (StringUtils.isNotEmpty(key)) {
            key = KEY;
        }
        byte[] bytes = null;
        String encryptStr = "";
        try {
            bytes = encrypt(src.getBytes(), key.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (bytes != null) {
            encryptStr = byte2hex(bytes);
        }
        return encryptStr;
    }

    public static String encode(String src) {
        return encode(src, KEY);
    }

    public static String getDecodeMAC(String decryptStr) {
        String mac = "";
        String[] strs = decryptStr.split(":");
        if (strs.length == 5) {
            mac = strs[0];
        }
        return mac;
    }

    public static Long decodeAsLong(String src) {
        String result = decode(src);
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(encode("11"));
        System.out.println(decode(""));
        System.out.println(System.currentTimeMillis()/1000);
        System.out.println((int)(Math.ceil(Math.random()*10+80)));
        System.out.println(getStudyTimekSurpassRate());
    }

    private static String getStudyTimekSurpassRate() {
        final int max = 90;
        final int min = 80;
        return  (int) (Math.ceil(Math.random() * (max - min) + min))+"%";

    }
}