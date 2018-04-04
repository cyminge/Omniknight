package com.cy.omniknight.tools.bitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.*;

/**
 * 图像压缩
 * 
 * @author JLB6088
 * 
 */
@SuppressLint("NewApi")
public class BitmapCompress {
    
    private static final String TAG = "BitmapCompress";
    private static final int HIGH_QUALITY = 100;
    private static final int LOW_QUALITY = 50;
    public static final int KB = 1024;
    public static final int MB = KB * KB;
    private static final int MAX_SIZE = MB;

    public static boolean fullCompressImageFile(String srcPath, File targetFile, int targetWidth,
            int targetHeight, long maxSize) {
        Bitmap bitmap = decodeBySampleSize(srcPath, targetWidth, targetHeight);
        return qualityCompressBitmapToFile(bitmap, targetFile, maxSize);
    }

    public static Bitmap decodeBySampleSize(String srcPath, int targetWidth, int targetHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapUtils.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = computeSampleSize(newOpts, targetWidth, targetHeight);
        return BitmapUtils.decodeFile(srcPath, newOpts);
    }

    public static Bitmap fullCompressBitmap(Bitmap srcBitmap, int targetWidth, int targetHeight, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream isBm = null;
        try {
            srcBitmap.compress(CompressFormat.JPEG, HIGH_QUALITY, baos);
            if (baos.toByteArray().length > MAX_SIZE) {
                baos.reset();
                srcBitmap.compress(CompressFormat.JPEG, LOW_QUALITY, baos);
            }

            isBm = new ByteArrayInputStream(baos.toByteArray());
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapUtils.decodeStream(isBm, null, newOpts);
            newOpts.inJustDecodeBounds = false;
            newOpts.inSampleSize = computeSampleSize(newOpts, targetWidth, targetHeight);
            newOpts.inPreferredConfig = Config.RGB_565;
            isBm = new ByteArrayInputStream(baos.toByteArray());
            Bitmap bitmap = BitmapUtils.decodeStream(isBm, null, newOpts);

            return getQualityCompressBitmap(bitmap, maxSize);
        } catch (Exception e) {
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
            }

            if (isBm != null) {
                try {
                    isBm.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public static int computeSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight) {
        return computeSampleSize(options, targetWidth, targetHeight, true);
    }

    public static int computeSampleSize(BitmapFactory.Options options, int targetWidth, int targetHeight,
            boolean isMin) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            final int heightRatio = Math.round((float) height / targetHeight);
            final int widthRatio = Math.round((float) width / targetWidth);
            if (isMin) {
                inSampleSize = Math.min(heightRatio, widthRatio);
            } else {
                inSampleSize = Math.max(heightRatio, widthRatio);
            }
        }
        return inSampleSize;
    }

    private static Bitmap getQualityCompressBitmap(Bitmap image, long maxSize) {
        if (maxSize == 0) {
            return image;
        }

        byte[] thumbData = bmpToByteArray(image, false);
        if (thumbData.length <= maxSize) {
            Log.e("cyTest", "-->lalala");
            return image;
        }

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inPreferredConfig = Config.RGB_565;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteArrayInputStream isBm = null;
        try {
            int quality = HIGH_QUALITY;
            int step = 5;
            Bitmap bitmap = null;
            int count = 0;
            do {
                count++;
                baos.reset();
                image.compress(CompressFormat.JPEG, quality, baos);
                isBm = new ByteArrayInputStream(baos.toByteArray());
                if (quality <= step) {
                    step = 1;
                }
                quality -= step;
                if (quality <= 0) {
                    break;
                }
                bitmap = BitmapUtils.decodeStream(isBm, null, newOpts);
                thumbData = bmpToByteArray(bitmap, false);

                isBm.close();
            } while (thumbData.length > maxSize);
            Log.e("cyTest", "count-->" + count);
            return bitmap;
        } catch (IOException e) {
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                    baos = null;
                } catch (IOException e) {
                }
            }

            if (isBm != null) {
                try {
                    isBm.close();
                    isBm = null;
                } catch (IOException e) {
                }
            }

            BitmapUtils.recycleBitmap(image);
        }
        return null;
    }

    private static ByteArrayOutputStream compressBitmapByQuality(Bitmap image, long maxSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, HIGH_QUALITY, baos);
        int quality = HIGH_QUALITY;
        int step = 5;
        do {
            baos.reset();
            image.compress(CompressFormat.JPEG, quality, baos);
            if (quality <= step) {
                step = 1;
            }
            quality -= step;
            if (quality <= 0) {
                break;
            }
        } while (baos.toByteArray().length > maxSize);
        return baos;
    }

    private static boolean qualityCompressBitmapToFile(Bitmap bitmap, File targetFile, long maxSize) {
        ByteArrayOutputStream baos = null;
        FileOutputStream fos = null;
        try {
            baos = compressBitmapByQuality(bitmap, maxSize);
            fos = new FileOutputStream(targetFile);
            fos.write(baos.toByteArray());
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.JPEG, HIGH_QUALITY, output);
        if (needRecycle) {
            BitmapUtils.recycleBitmap(bmp);
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
        }

        return result;
    }

}
