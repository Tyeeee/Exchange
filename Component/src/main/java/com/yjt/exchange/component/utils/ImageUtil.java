package com.hynet.heebit.components.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.hynet.heebit.components.constant.Regex;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

public class ImageUtil {
     
    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + Regex.LEFT_SLASH.getRegext() + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     *
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static byte[] getQRCode(String data, int size) {
        try {
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
            return getBytesFromBitMatrix(bitMatrix);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getBytesFromBitMatrix(BitMatrix bits) {
        if (bits == null) return null;

        int h = bits.getHeight();
        int w = (bits.getWidth() + 7) / 8;
        byte[] rv = new byte[h * w + 8];

        rv[0] = 0x1D;
        rv[1] = 0x76;
        rv[2] = 0x30;
        rv[3] = 0x00;

        rv[4] = (byte) w;//xL
        rv[5] = (byte) (w >> 8);//xH
        rv[6] = (byte) h;
        rv[7] = (byte) (h >> 8);

        int k = 8;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                for (int n = 0; n < 8; n++) {
                    byte b = getBitMatrixColor(bits, j * 8 + n, i);
                    rv[k] += rv[k] + b;
                }
                k++;
            }
        }
        return rv;
    }

    private static byte getBitMatrixColor(BitMatrix bits, int x, int y) {
        int width = bits.getWidth();
        int height = bits.getHeight();
        if (x >= width || y >= height || x < 0 || y < 0) return 0;
        if (bits.get(x, y)) {
            return 1;
        } else {
            return 0;
        }
    }

    public static Bitmap create2DCoderBitmap(String url, int w, int h, Bitmap logo) throws WriterException {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Bitmap scaleLogo = getScaleLogo(logo, w, h);

        int offsetX = w / 2;
        int offsetY = h / 2;

        int scaleWidth = 0;
        int scaleHeight = 0;
        if (scaleLogo != null) {
            scaleWidth = scaleLogo.getWidth();
            scaleHeight = scaleLogo.getHeight();
            offsetX = (w - scaleWidth) / 2;
            offsetY = (h - scaleHeight) / 2;
        }
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0);
        BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, w, h, hints);
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x >= offsetX && x < offsetX + scaleWidth && y >= offsetY && y < offsetY + scaleHeight) {
                    int pixel = scaleLogo.getPixel(x - offsetX, y - offsetY);
                    if (pixel == 0) {
                        if (bitMatrix.get(x, y)) {
                            pixel = 0xff000000;
                        } else {
                            pixel = 0xffffffff;
                        }
                    }
                    pixels[y * w + x] = pixel;
                } else {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    private static Bitmap getScaleLogo(Bitmap logo, int w, int h) {
        if (logo == null) return null;
        Matrix matrix = new Matrix();
        float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 / logo.getHeight());
        matrix.postScale(scaleFactor, scaleFactor);
        Bitmap result = Bitmap.createBitmap(logo, 0, 0, logo.getWidth(), logo.getHeight(), matrix, true);
        return result;
    }

    /**
     * 保存图片到SD卡.
     */
    public static void saveBitmap(Bitmap mBitmap, File file, String name) {
        if (!file.exists()) {
            file.mkdirs();
        }
        File f = new File(file, name + Regex.IMAGE_JPG.getRegext());
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(fOut);
        }
    }

    public static void closeQuietly(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
    }

    public static String bitmapToBase64(Bitmap bitmap, int quality) {
        if (bitmap != null) {
            ByteArrayOutputStream outputStream = null;
            try {
                outputStream = new ByteArrayOutputStream();
                if (quality >= 0) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                }
                outputStream.flush();
                outputStream.close();
//            return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
                return Base64Util.encode(outputStream.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            return null;
        }
    }

    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64Util.decode(base64Data);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {
                bytes[i] += 256;
            }
        }
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
