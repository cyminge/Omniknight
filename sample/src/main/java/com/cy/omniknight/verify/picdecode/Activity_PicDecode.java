package com.cy.omniknight.verify.picdecode;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.cy.omniknight.BaseActivity;
import com.cy.omniknight.R;

@SuppressLint("NewApi")
public class Activity_PicDecode extends BaseActivity implements OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_decode);


        // 测试图片的压缩
        ImageView img_ora = (ImageView) findViewById(R.id.img_ora);
        ImageView img_new = (ImageView) findViewById(R.id.img_new);
        img_new.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});

        Resources resource = getResources();
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap rgb8888 = BitmapFactory.decodeResource(resource, R.drawable.root_bg, opts);
        // img_ora.setImageBitmap(rgb8888);

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.root_bg, newOpts);
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = BitmapCompress.computeSampleSize(newOpts, 250, 250);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.root_bg, newOpts);
        img_ora.setImageBitmap(bitmap);

        // opts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap rgb565 = BitmapFactory.decodeResource(resource, R.drawable.root_bg, opts);
        // img_new.setImageBitmap(rgb565);
//        Log.e("cyTest", "=======================================================");
        big(bitmap, img_new);

        // 测试图片的压缩 end

    }

    private int displayWidth;
    private int displayHeight;
    private float scaleWidth = 1;
    private float scaleHeight = 1;

    /* 图片放大的method */
    private void big(Bitmap bmp, ImageView img_new) {
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        /* 设置图片放大的比例 */
        double scale = 2;
        /* 计算这次要放大的比例 */
        scaleWidth = (float) (scaleWidth * scale);

        scaleHeight = (float) (scaleHeight * scale);

        /* 产生reSize后的Bitmap对象 */
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

        img_new.setImageBitmap(resizeBmp);

        // if (id == 0) {
        // /* 如果是第一次按，就删除原来设置的ImageView */
        // layout1.removeView(mImageView);
        // } else {
        // /* 如果不是第一次按，就删除上次放大缩小所产生的ImageView */
        // layout1.removeView((ImageView) findViewById(id));
        // }
        // /* 产生新的ImageView，放入reSize的Bitmap对象，再放入Layout中 */
        // id++;
        // ImageView imageView = new ImageView(EX04_23.this);
        // imageView.setId(id);
        // imageView.setImageBitmap(resizeBmp);
        // layout1.addView(imageView);
        // setContentView(layout1);
        //
        // /* 如果再放大会超过屏幕大小，就把Button disable */
        // if (scaleWidth * scale * bmpWidth > displayWidth ||
        //
        // scaleHeight * scale * bmpHeight > displayHeight)
        //
        // {
        // mButton02.setEnabled(false);
        // }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn) {
//            Log.e("cyTest", "aaaa == " + getResources().getResourceEntryName(R.id.btn));

        } else if(view.getId() == R.id.shape_test) {
        	
        }
    }

}
