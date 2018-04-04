package com.cy.omniknight.tracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.cy.omniknight.R;
import com.cy.omniknight.tracer.util.FileUtils;

import java.io.File;


public class TracerActivity extends Activity {

	private TextView mShowDebug;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        mShowDebug = (TextView) findViewById(R.id.show_debug);
//        if(TracerHelper.isNeedExportLog()) {
//        	mShowDebug.setText("日誌開關已打開");
//        } else {
//        	mShowDebug.setText("日誌開關已關閉");
//        }
    }
    
    private static final String LOG_FILE = "gionee0123456789logdebug";
    
    public void testExportLog(View view) {
    	if(!FileUtils.isSDCardMounted()) {
    		return;
    	}
    	
    	Intent intent = new Intent(TracerHelper.BROADCAST_ACTION_LOG_EXPORT);
    	if(TracerHelper.isNeedExportLog()) {
    		String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + LOG_FILE;
        	File file = new File(filePath);
        	if(file.exists()) {
        		file.delete();
        	}
    		intent.putExtra(TracerHelper.BROADCAST_EXTRA_IS_EXPORT_LOG_KEY, false);
    	} else {
    		String filePath = Environment.getExternalStorageDirectory().getPath() + File.separator + LOG_FILE;
        	File file = new File(filePath);
        	if(!file.exists()) {
        		file.mkdirs();
        	}
    		intent.putExtra(TracerHelper.BROADCAST_EXTRA_IS_EXPORT_LOG_KEY, true);
    	}
		getApplicationContext().sendOrderedBroadcast(intent, null);
    	
    	if(TracerHelper.isNeedExportLog()) {
        	mShowDebug.setText("日誌開關已打開");
        } else {
        	mShowDebug.setText("日誌開關已關閉");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }
}
