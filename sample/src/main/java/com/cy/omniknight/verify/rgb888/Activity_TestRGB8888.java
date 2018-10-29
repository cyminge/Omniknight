package com.cy.omniknight.verify.rgb888;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;

import com.cy.omniknight.R;


@TargetApi(Build.VERSION_CODES.KITKAT)
public class Activity_TestRGB8888 extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rgb8888);
		
		mHandler.sendEmptyMessageDelayed(9527, 120);
		
	}
	
	private static final int MAX_THUMB_SIZE = 32 * 1024;
	
	Handler mHandler = new Handler() {
	    public void handleMessage(android.os.Message msg) {
	        
	        
	        ImageView img_ora = (ImageView) findViewById(R.id.img_ora);
	        ImageView img_new = (ImageView) findViewById(R.id.img_new);
	        ImageView img_final = (ImageView) findViewById(R.id.img_final);
	        
	        Resources resource = getResources();
	        BitmapFactory.Options opts = new BitmapFactory.Options();
	        Bitmap rgb8888 = BitmapFactory.decodeResource(resource, R.drawable.root_bg, opts);
	        img_ora.setImageBitmap(rgb8888);
	        
	        opts.inPreferredConfig = Bitmap.Config.RGB_565;
	        Bitmap rgb565 = BitmapFactory.decodeResource(resource, R.drawable.root_bg, opts);
//	        img_new.setImageBitmap(rgb565);
	        Log.e("cyTest", "=======================================================");
	        Bitmap thumb = BitmapCompress.fullCompressBitmap(rgb565, 150, 150, MAX_THUMB_SIZE);
	        if (thumb != null) {
	            Log.e("cyTest", "thumb.name-->"+thumb.getConfig().name());
                Log.e("cyTest", "thumb.AllocationByteCount1-->"+thumb.getAllocationByteCount());
                Log.e("cyTest", "thumb.AllocationByteCount2-->"+thumb.getWidth()*thumb.getHeight()*4);
//                Log.e("cyTest", "thumb.byteArray-->"+BitmapCompress.bmpToByteArray(thumb, true).length);
                img_new.setImageBitmap(thumb);
                img_final.setImageBitmap(thumb);
            }
	        TypedValue mTypedValue = new TypedValue();
	        resource.getValue(R.drawable.root_bg, mTypedValue, true);
	        Log.e("cyTest", "mTypedValue-->"+mTypedValue);

	        
	    };
	};

}
