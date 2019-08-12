package com.hynet.heebit.components.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.hynet.heebit.components.constant.Regex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class StringUtil {

    private StringUtil() {
        // cannot be instantiated
    }

    public static String append(boolean isSeparate, String... content) {
        StringBuffer buffer = new StringBuffer();
        if (isSeparate) {
            for (int i = 0; i < content.length; i++) {
                if (i != content.length - 1) {
                    buffer.append(content[i]).append(Regex.COMMA.getRegext());
                } else {
                    buffer.append(content[i]);
                }
            }
        } else {
            for (String str : content) {
                buffer.append(str);
            }
        }
         LogUtil.Companion.getInstance().print(buffer.toString());
        return buffer.toString();
    }

    public static String append(boolean isSeparate, List<String> contents) {
        if (contents != null && contents.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            if (isSeparate) {
                for (int i = 0; i < contents.size(); i++) {
                    if (i != contents.size() - 1) {
                        buffer.append(contents.get(i)).append(Regex.COMMA.getRegext());
                    } else {
                        buffer.append(contents.get(i));
                    }
                }
            } else {
                for (int i = 0; i < contents.size(); i++) {
                    buffer.append(contents.get(i));
                }
            }
             LogUtil.Companion.getInstance().print(buffer.toString());
            return buffer.toString();
        } else {
            return Regex.NONE.getRegext();
        }
    }

    public static String toUpperCase4Start(String string) {
        char[] charArray = string.toCharArray();
        charArray[0] = toUpperCase(charArray[0]);
        return String.valueOf(charArray);
    }

    private static char toUpperCase(char chars) {
        if (97 <= chars && chars <= 122) {
            chars ^= 32;
        }
        return chars;
    }

    /**
     * 功能描述：去除字符串首部为"0"字符
     *
     * @param content 传入需要转换的字符串
     * @return 转换后的字符串
     */
    public static String removeZero(String content) {
        char ch;
        String result = "";
        if (content != null && content.trim().length() > 0 && !content.trim().equalsIgnoreCase("null")) {
            try {
                for (int i = 0; i < content.length(); i++) {
                    ch = content.charAt(i);
                    if (ch != '0') {
                        result = content.substring(i);
                        break;
                    }
                }
            } catch (Exception e) {
                result = "";
            }
        } else {
            result = "";
        }
        return result;
    }

    /**
     * 功能描述：金额字符串转换：单位分转成单元
     *
     * @param content 传入需要转换的金额字符串
     * @return 转换后的金额字符串
     */
    public static String fenToYuan(String content) {
        if (content == null)
            return "0.00";
        String s = content.toString();
        int len = -1;
        StringBuilder sb = new StringBuilder();
        if (s != null && s.trim().length() > 0 && !s.equalsIgnoreCase("null")) {
            s = removeZero(s);
            if (s != null && s.trim().length() > 0 && !s.equalsIgnoreCase("null")) {
                len = s.length();
                int tmp = s.indexOf("-");
                if (tmp >= 0) {
                    if (len == 2) {
                        sb.append("-0.0").append(s.substring(1));
                    } else if (len == 3) {
                        sb.append("-0.").append(s.substring(1));
                    } else {
                        sb.append(s.substring(0, len - 2)).append(".").append(s.substring(len - 2));
                    }
                } else {
                    if (len == 1) {
                        sb.append("0.0").append(s);
                    } else if (len == 2) {
                        sb.append("0.").append(s);
                    } else {
                        sb.append(s.substring(0, len - 2)).append(".").append(s.substring(len - 2));
                    }
                }
            } else {
                sb.append("0.00");
            }
        } else {
            sb.append("0.00");
        }
        return sb.toString();
    }

    /**
     * 功能描述：金额字符串转换：单位元转成单分
     *
     * @param content 传入需要转换的金额字符串
     * @return 转换后的金额字符串
     */
    public static String yuanToFen(String content) {
        if (content == null)
            return "0";
        String s = content.toString();
        int posIndex = -1;
        String str = "";
        StringBuilder sb = new StringBuilder();
        if (s != null && s.trim().length() > 0 && !s.equalsIgnoreCase("null")) {
            posIndex = s.indexOf(".");
            if (posIndex > 0) {
                int len = s.length();
                if (len == posIndex + 1) {
                    str = s.substring(0, posIndex);
                    if (str == "0") {
                        str = "";
                    }
                    sb.append(str).append("00");
                } else if (len == posIndex + 2) {
                    str = s.substring(0, posIndex);
                    if (str == "0") {
                        str = "";
                    }
                    sb.append(str).append(s.substring(posIndex + 1, posIndex + 2)).append("0");
                } else if (len == posIndex + 3) {
                    str = s.substring(0, posIndex);
                    if (str == "0") {
                        str = "";
                    }
                    sb.append(str).append(s.substring(posIndex + 1, posIndex + 3));
                } else {
                    str = s.substring(0, posIndex);
                    if (str == "0") {
                        str = "";
                    }
                    sb.append(str).append(s.substring(posIndex + 1, posIndex + 3));
                }
            } else {
                sb.append(s).append("00");
            }
        } else {
            sb.append("0");
        }
        str = removeZero(sb.toString());
        if (str != null && str.trim().length() > 0 && !str.trim().equalsIgnoreCase("null")) {
            return str;
        } else {
            return "0";
        }
    }

    /**
     * 约束结果为两位小数
     */
    public static String boundTwoDecimal(String num) {
        if (!TextUtils.isEmpty(num)) {
            if (num.contains(".")) {
                int pointIndex = num.lastIndexOf(".");
                String decimal = num.substring(pointIndex + 1);
                if (decimal.length() == 0) {
                    return num + "00";
                } else if (decimal.length() == 1) {
                    return num + "0";
                } else if (decimal.length() >= 2) {
                    return num.substring(0, pointIndex + 3);
                } else {
                    return num;
                }
            } else {
                return num + ".00";
            }
        } else {
            return "";
        }
    }

    /**
     * 校验小数点后是否多于两位
     */
    public static boolean isTwoDecimal(String num) {
        if (num.contains(".")) {
            int pointIndex = num.lastIndexOf(".");
            String decimal = num.substring(pointIndex + 1);
            if (decimal.length() > 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * 银行卡保留前六位和后四位。
     */
    public static String bankCard(String bankCardNumber) {
        int length = bankCardNumber.length();
        if (length > 10) {
            bankCardNumber = bankCardNumber.replaceAll("(.{" + (length < 12 ? 3 : 6) + "})(.*)(.{4})", "$1" + "****" + "$3");
//            bankCardCode = bankCardCode.substring(0, 6) + "*******" + bankCardCode.substring(length - 4);
        }
        return bankCardNumber;
    }

    /**
     * @功能 保证显示的金额存在￥
     * @实现 判断字符串是否有￥，没有￥则在字符串前方拼接￥
     * @参数 money 要显示的金额
     * @返回值 String 带￥的金额字符串
     */
    public static String getMoney(String money) {
        if (!TextUtils.isEmpty(money) && !money.contains(Regex.YUAN.getRegext())) {
            return Regex.YUAN.getRegext() + money;
        } else {
            return money;
        }
    }

    public static String getJson(Context context, String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String byte2string(byte[] b) {
        String asci = "";
        for (int i = 0; i < b.length; ++i) {
            asci += String.format("%02X", b[i]);
        }
        return asci;
    }

    public static byte[] string2byte(String key) {
        if (key == null) {
            return null;
        }
        int keyLength = key.length();
        //转为bcd码，和62域一致
        if (keyLength % 2 == 1) {            // 长度奇数
            key = key + "0";        //左对齐
        }
        int bcdLength = (keyLength + 1) / 2;
        byte[] bcdByte = new byte[bcdLength];
        for (int i = 0; i < keyLength; i += 2) {
            bcdByte[i / 2] = (byte) Integer.decode("0x" + key.substring(i, i + 2)).intValue();
        }
        return bcdByte;
    }
}
