package com.hynet.heebit.components.utils;

import android.text.TextUtils;

import com.hynet.heebit.components.constant.Constant;
import com.hynet.heebit.components.constant.Regex;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecurityUtil {

    private static SecurityUtil securityUtil;

    private SecurityUtil() {
        // cannot be instantiated
    }

    public static synchronized SecurityUtil getInstance() {
        if (securityUtil == null) {
            securityUtil = new SecurityUtil();
        }
        return securityUtil;
    }

    public static void releaseInstance() {
        if (securityUtil != null) {
            securityUtil = null;
        }
    }

    public char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? Constant.Data.DIGITS_LOWER : Constant.Data.DIGITS_UPPER);
    }

    public String encodeHexStr(byte[] data) {
        return encodeHexString(data, true);
    }

    public String encodeHexString(byte[] data, boolean toLowerCase) {
        return encodeHexString(data, toLowerCase ? Constant.Data.DIGITS_LOWER : Constant.Data.DIGITS_UPPER);
    }

    private String encodeHexString(byte[] data, char[] toDigits) {
        return String.valueOf(encodeHex(data, toDigits));
    }

    private char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public byte[] decodeHex(char[] data) {
        int len = data.length;
        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];
        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(data[j], j) << 4;
            j++;
            f = f | toDigit(data[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }
        return out;
    }

    private int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }

    public String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte high = (byte) ((bytes[i] & 0xf0) >> 4);
            byte low = (byte) (bytes[i] & 0x0f);
            builder.append(nibble2char(high));
            builder.append(nibble2char(low));
        }
        return builder.toString();
    }

    public byte[] hexStringToByte(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        int len = data.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; ++i) {
            char ch1 = data.charAt(i * 2);
            char ch2 = data.charAt(i * 2 + 1);
            result[i] = (byte) ((charToHex(ch1) << 4) + charToHex(ch2));
        }
        return result;
    }

    public byte charToHex(char data) {
        if (data >= '0' && data <= '9')
            return (byte) (data - '0');
        if (data >= 'a' && data <= 'f')
            return (byte) (10 + (data - 'a'));
        if (data >= 'A' && data <= 'F')
            return (byte) (10 + (data - 'A'));
        return 0;
    }

    private int byteToInt(byte b, byte c) {
        short s0 = (short) (c & 0xff);
        short s1 = (short) (b & 0xff);
        s1 <<= 8;
        return (short) (s0 | s1);
    }

    private byte[] intToByte(int res) {
        byte[] targets = new byte[2];
        targets[1] = (byte) (res & 0xff);
        targets[0] = (byte) ((res >> 8) & 0xff);
        return targets;
    }

    private byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static char nibble2char(byte data) {
        byte nibble = (byte) (data & 0x0f);
        if (nibble < 10) {
            return (char) ('0' + nibble);
        }
        return (char) ('A' + nibble - 10);
    }

    /************************************************/

    public byte[] encryptDes(byte[] data, String key, String alg, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(alg);
        if (alg.contains("/CBC/") && iv != null) {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance(Constant.Data.ALGORITHM0).generateSecret(new DESKeySpec(key.getBytes(Regex.UTF_8.getRegext()))), new IvParameterSpec(iv));
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance(Constant.Data.ALGORITHM0).generateSecret(new DESKeySpec(key.getBytes(Regex.UTF_8.getRegext()))));
        }
        return cipher.doFinal(data);
    }

    public byte[] decryptDes(byte[] data, String key, String alg, byte[] iv) throws NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(alg);
        if (alg.contains("/CBC/") && iv != null) {
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance(Constant.Data.ALGORITHM0).generateSecret(new DESKeySpec(key.getBytes(Regex.UTF_8.getRegext()))), new IvParameterSpec(iv));
        } else {
            cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance(Constant.Data.ALGORITHM0).generateSecret(new DESKeySpec(key.getBytes(Regex.UTF_8.getRegext()))));
        }
        return cipher.doFinal(data);
    }

    public String encrypt3Des(String message, String key, String format) throws UnsupportedEncodingException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        String k1 = key.substring(0, 8);
        String k2 = key.substring(8, 16);
        String k3 = key.substring(16, 24);
        boolean bHex16 = "ToHex16".equalsIgnoreCase(format);
        byte[] msg = message.getBytes(Regex.UTF_8.getRegext());
        int newLen = (msg.length + 8) & (~0x07);
        if (newLen != msg.length) {
            int k;
            byte[] msg2 = new byte[newLen];
            for (k = 0; k < msg.length; ++k)
                msg2[k] = msg[k];
            for (k = msg.length; k < msg2.length; ++k)
                msg2[k] = (byte) (newLen - msg.length);
            msg = msg2;
        }

        byte[] data = new byte[msg.length];
        byte[] iv = new byte[8];
        int i;

        for (i = 0; i + 8 <= msg.length; i += 8) {
            byte[] data1 = new byte[8];
            int j;
            for (j = 0; j < 8; ++j)
                data1[j] = msg[i + j];

            if (i == 0)
                iv = k1.getBytes(Regex.UTF_8.getRegext());
            else
                for (j = 0; j < 8; ++j)
                    iv[j] = data[i + j - 8];

            data1 = encryptDes(data1, k1, Constant.Data.ALGORITHM_CBC, iv);
            data1 = decryptDes(data1, k2, Constant.Data.ALGORITHM_ECB, null);
            data1 = encryptDes(data1, k3, Constant.Data.ALGORITHM_ECB, null);
            for (j = 0; j < 8; ++j)
                data[i + j] = data1[j];
        }

        String result;
        if (bHex16)
            result = bytesToHexString(data);
        else
            result = Base64Util.encode(data);
        return result;
    }

    public String decrypt3Des(String message, String key, String format) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, InvalidKeySpecException {
        String k1 = key.substring(0, 8);
        String k2 = key.substring(8, 16);
        String k3 = key.substring(16, 24);
        byte[] iv = new byte[8];
        int i;
        boolean bHex16 = "ToHex16".equalsIgnoreCase(format);
        byte[] msg;
        if (bHex16)
            msg = hexStringToByte(message);
        else
            msg = Base64Util.decode(message);
        byte[] data = new byte[msg.length];

        for (i = 0; i + 8 <= msg.length; i += 8) {
            byte[] data1 = new byte[8];
            int j;
            for (j = 0; j < 8; ++j)
                data1[j] = msg[i + j];

            data1 = decryptDes(data1, k3, Constant.Data.ALGORITHM_ECB, null);
            data1 = encryptDes(data1, k2, Constant.Data.ALGORITHM_ECB, null);

            if (i == 0)
                iv = k1.getBytes(Regex.UTF_8.getRegext());
            else
                for (j = 0; j < 8; ++j)
                    iv[j] = msg[i + j - 8];

            data1 = decryptDes(data1, k1, Constant.Data.ALGORITHM_CBC, iv);
            for (j = 0; j < 8; ++j)
                data[i + j] = data1[j];
        }

        // 去掉尾部的padding
        byte val = data[data.length - 1];
        if (val > 0 && val <= data.length) {
            if (val == data.length)
                return "";

            byte[] data2 = new byte[data.length - val];
            // Arrays.copyOf( data, data.length-val);
            for (i = 0; i < data2.length; ++i) {
                data2[i] = data[i];
            }
            return new String(data2, Regex.UTF_8.getRegext());
        }
        return new String(data, Regex.UTF_8.getRegext());
    }

    public byte[] des3EncodeECB(byte[] key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)));
        return cipher.doFinal(data);
    }

    public byte[] des3DecodeECB(byte[] key, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("desede/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)));
        return cipher.doFinal(data);
    }

    public byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)), new IvParameterSpec(keyiv));
        return cipher.doFinal(data);
    }

    public byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(key)), new IvParameterSpec(keyiv));
        return cipher.doFinal(data);
    }

    public PublicKey getRsaPublicKey(String key) throws CertificateException {
        return CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(hexStringToByte(key))).getPublicKey();
    }

    public byte[] encryptRSA(String publicKey, String data) throws CertificateException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, UnsupportedEncodingException, InvalidKeyException {
        return encryptRSA(getRsaPublicKey(publicKey), data);
    }

    public byte[] encryptRSA(PublicKey publicKey, String data) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes(Regex.UTF_8.getRegext()));
    }

//    public String encryptAES(String content, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
//        KeyGenerator.getInstance(Regex.AES.getRegext()).initialize(128, new SecureRandom(key.getBytes()));
//        Cipher cipher = Cipher.getInstance(Regex.AES.getRegext());
//        cipher.initialize(Cipher.ENCRYPT_MODE, new SecretKeySpec(KeyGenerator.getInstance(Regex.AES.getRegext()).generateKey().getEncoded(), Regex.AES.getRegext()));
//        return bytesToHexString(cipher.doFinal(content.getBytes(Regex.UTF_8.getRegext())));
//    }

//    public String decryptAES(String content, String key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
//        KeyGenerator.getInstance(Regex.AES.getRegext()).initialize(128, new SecureRandom(key.getBytes()));
//        Cipher cipher = Cipher.getInstance(Regex.AES.getRegext());
//        cipher.initialize(Cipher.DECRYPT_MODE, new SecretKeySpec(KeyGenerator.getInstance(Regex.AES.getRegext()).generateKey().getEncoded(), Regex.AES.getRegext()));
//        return new String(cipher.doFinal(hexStringToByte(content)), Regex.UTF_8.getRegext());
//    }

    public byte[] encryptAES(String data, String key) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
         LogUtil.Companion.getInstance().print("data:" + data);
         LogUtil.Companion.getInstance().print("key:" + key);
        Cipher cipher = Cipher.getInstance(Regex.AES.getRegext());
        if (!TextUtils.isEmpty(data) && !TextUtils.isEmpty(key)) {
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key.getBytes(), Regex.AES.getRegext()));
            return cipher.doFinal(data.getBytes(Regex.UTF_8.getRegext()));
        } else {
            return null;
        }
    }

    public byte[] decryptAES(byte[] data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Regex.AES.getRegext());
        if (data != null && !TextUtils.isEmpty(key)) {
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), Regex.AES.getRegext()));
            return cipher.doFinal(data);
        } else {
            return null;
        }
    }

    public String encryptMD5(String data) {
        if (!TextUtils.isEmpty(data)) {
            return encryptMD5(data.getBytes());
        } else {
            return null;
        }
    }

    public String encryptMD5(byte[] buffer) {
        try {
            return bytesToHexString(MessageDigest.getInstance(Regex.MD5.getRegext()).digest(buffer));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptSHA(String algorithm, byte[] input) throws NoSuchAlgorithmException {
        return bytesToHexString(MessageDigest.getInstance(algorithm).digest(input));
    }

    public String encryptSHA(String algorithm, String data) throws NoSuchAlgorithmException {
        return encryptSHA(algorithm, data.getBytes());
    }

    public String encryptSHA1(byte[] input) throws NoSuchAlgorithmException {
        return encryptSHA(Regex.SHA_1.getRegext(), input);
    }

    public String encryptSHA1(String data) throws NoSuchAlgorithmException {
        return encryptSHA1(data.getBytes());
    }

    public String encryptSHA256(String data) throws NoSuchAlgorithmException {
        return encryptSHA(Regex.SHA_256.getRegext(), data);
    }

    public String encryptSHA256WithSalt(String data) throws NoSuchAlgorithmException {
        return data.substring(0, 32) + encryptSHA(Regex.SHA_256.getRegext(), data);
    }
    
    public String encryptSHA256WithSalt(String data, String salt) throws NoSuchAlgorithmException {
        return salt + encryptSHA(Regex.SHA_256.getRegext(), salt + data);
    }

    static {
        System.loadLibrary("encrypt");
    }

    public static native String encryptAES(String json, String key, boolean isEncrypt, int type);

    /**************************************************************************/

    byte[] iv = {0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private byte[] pbkdf2(char[] password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(password, salt, iterations, bytes * 8)).getEncoded();
    }

    private byte[] encrypt(byte[] data, byte[] key) {
        try {
            int base = 16;
            if (key.length % base != 0) {
                int groups = key.length / base + (key.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(key, 0, temp, 0, key.length);
                key = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] decrypt(byte[] data, byte[] key) {
        try {
            int base = 16;
            if (key.length % base != 0) {
                int groups = key.length / base + (key.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(key, 0, temp, 0, key.length);
                key = temp;
            }
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchProviderException | BadPaddingException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String generateRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(base.charAt(random.nextInt(base.length())));
        }
        return stringBuilder.toString();
    }

    public String generateEncryptString(String data, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = generateRandomString(32);
        byte[] encryptData = pbkdf2(password.toCharArray(), salt.getBytes(), 1000, 32);
        return salt + new String(Hex.encode(encrypt(data.getBytes(), encryptData)));
    }

    public String generateDecryptString(String data, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String salt = data.substring(0, 32);
        byte[] encryptData = pbkdf2(password.toCharArray(), salt.getBytes(), 1000, 32);
        byte[] decryptData = decrypt(Hex.decode(data.substring(32, data.length())), encryptData);
        return new String(decryptData);
    }

    /**************************************************************************/
}
