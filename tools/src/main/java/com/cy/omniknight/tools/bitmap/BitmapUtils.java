package com.cy.omniknight.tools.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BitmapUtils {
	
    public static Bitmap decodeFile(String pathName) {
        return decodeFile(pathName, null);
    }

    public static Bitmap decodeFile(String pathName, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeFile(pathName, opts);
        if (opts == null && bitmap == null) {
        }
        return bitmap;
    }

    public static Bitmap decodeStream(InputStream is, Rect outPadding, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeStream(is, outPadding, opts);
        return bitmap;
    }

    public static Bitmap decodeResource(Resources res, int id) {
        return decodeResource(res, id, null);
    }

    public static Bitmap decodeResource(Resources res, int id, BitmapFactory.Options opts) {
        Bitmap bitmap = BitmapFactory.decodeResource(res, id, opts);
        return bitmap;
    }

    public static Bitmap decodeByteArray(byte[] data, int offset, int length) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, offset, length);
        return bitmap;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (!isBitmapEmpty(bitmap)) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static boolean isBitmapEmpty(Bitmap bitmap) {
        return null == bitmap || bitmap.isRecycled();
    }

    protected Bitmap resizeBitmap(Bitmap bitmap, View view) {
        if (view == null || bitmap == null) {
            return bitmap;
        }

        int desWidth = view.getWidth();
        int desHeight = view.getHeight();
        return extractThumbnailIfNeed(bitmap, desWidth, desHeight);
    }

    public static Bitmap extractThumbnailIfNeed(Bitmap source, int desWidth, int desHeight) {
        if (needExtract(source, desWidth, desHeight)) {
            return extractThumbnail(source, desWidth, desHeight);
        } else {
            return source;
        }
    }

    private static final int MAX_DIFFER_PX = 100;

    private static boolean needExtract(Bitmap source, int desWidth, int desHeight) {
        if (desWidth <= 0 || desHeight <= 0 || source == null) {
            return false;
        }
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        return sourceWidth > desWidth + MAX_DIFFER_PX || sourceHeight > desHeight + MAX_DIFFER_PX;
    }

    public static Bitmap extractThumbnail(Bitmap source, int desWidth, int desHeight) {
        if (desWidth <= 0 || desHeight <= 0) {
            return source;
        }

        return cropBitmap(source, desWidth, desHeight);
    }

    public static final Paint HIGH_PAINT = new Paint(Paint.FILTER_BITMAP_FLAG);

    private static Bitmap cropBitmap(Bitmap source, int aimWidth, int aimHeight) {
        if (source == null) {
            return null;
        }
        float sourceWidth = source.getWidth();
        float sourceHeight = source.getHeight();
        if (sourceWidth == aimWidth && sourceHeight == aimHeight) {
            return source;
        }
        float offsetX = (aimWidth - sourceWidth) / 2f;
        float offsetY = (aimHeight - sourceHeight) / 2f;
        float scaleWidth = aimWidth / sourceWidth;
        float scaleHeight = aimHeight / sourceHeight;
        float scale = Math.max(scaleWidth, scaleHeight);

        Bitmap retBitmap = Bitmap.createBitmap(aimWidth, aimHeight, Config.ARGB_8888);
        Matrix matrix = new Matrix();
        Canvas canvas = new Canvas(retBitmap);
        matrix.postTranslate(offsetX, offsetY);
        matrix.postScale(scale, scale, aimWidth / 2f, aimHeight / 2f);
        canvas.drawBitmap(source, matrix, HIGH_PAINT);
        return retBitmap;
    }

    /**
     * 将两张位图拼接成一张
     * 
     * @param first
     * @param second
     * @return
     */
    public static Drawable add2Bitmap(Context context, Bitmap first, Bitmap second) {
        int width = first.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
        Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(first, 0, 0, null);
        canvas.drawBitmap(second, 4, 4, null);
        Drawable drawable = new BitmapDrawable(context.getResources(), result);
        return drawable;
    }

    /**
     * 得到旋转后的图片
     * 
     * @param path
     * @param bitmap
     * @return
     */
    public static Bitmap getRotatedBitmap(String path, Bitmap bitmap) {
        Bitmap rotatedBitmap = null;
        Matrix m = new Matrix();
        ExifInterface exif = null;
        int orientation = 1;

        try {
            if (path != null) {
                // Getting Exif information of the file
                exif = new ExifInterface(path);
            }
            if (exif != null) {
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    m.preRotate(270);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    m.preRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    m.preRotate(180);
                    break;
                }
                // Rotates the image according to the orientation

                rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m,
                        true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rotatedBitmap;
    }

    public static Bitmap getBitmapThumbnail(Bitmap bmp, int width, int height) {
        Bitmap bitmap = null;
        if (bmp != null) {
            int bmpWidth = bmp.getWidth();
            int bmpHeight = bmp.getHeight();
            if (width != 0 && height != 0) {
                Matrix matrix = new Matrix();
                float scaleWidth = ((float) width / bmpWidth);
                float scaleHeight = ((float) height / bmpHeight);
                matrix.postScale(scaleWidth, scaleHeight);
                bitmap = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
            } else {
                bitmap = bmp;
            }
        }
        // bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
        // ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 获取视频的缩略图 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。
     * 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
     * 
     * @param videoPath
     *            视频的路径
     * @param width
     *            指定输出视频缩略图的宽度
     * @param height
     *            指定输出视频缩略图的高度度
     * @param kind
     *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。
     *            其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return 指定大小的视频缩略图
     */
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        System.out.println("w" + bitmap.getWidth());
        System.out.println("h" + bitmap.getHeight());
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method = clazz.getMethod("setDataSource", String.class);
            method.invoke(instance, filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null)
                        return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
            }
        } catch (IllegalArgumentException ex) {
            // Assume this is a corrupt video file
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release").invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

}
