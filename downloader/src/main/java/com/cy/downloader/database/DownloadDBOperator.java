package com.cy.downloader.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cy.downloader.database.dao.DaoMaster;
import com.cy.downloader.database.dao.DownloadInfoDao;
import com.cy.downloader.database.entity.DownloadInfo;
import com.cy.omniknight.tools.SQLiteException;
import com.cy.omniknight.tools.Utils;

import java.util.ArrayList;

/**
 * Created by cy on 18-9-25.
 */

public class DownloadDBOperator {

    private static final String TAG = "DownloadDBOperator";

    private static final String DB_OWNER = "";
    private static String DB_PREFIX = "perpetual_download_conf";
    public static final int DB_MODE_WRITE = 1;
    public static final int DB_MODE_READ = 2;

    private Context mContext;
    private static SQLiteDatabase mDownloadSQLiteDatabase = null;

    public static DownloadDBOperator getInstance() {
        return DownloadDBHolder.DB_OPERATOR;
    }

    private static final class DownloadDBHolder {
        private static final DownloadDBOperator DB_OPERATOR = new DownloadDBOperator();
    }

    public DownloadDBOperator() {
        Context context = Utils.getApp();
        if(null == context) {
            throw new NullPointerException("global context must not be null !!");
        }
        mContext = context;
        if (mDownloadSQLiteDatabase == null) {
            setUp(getDaoDataBase(context,  DB_PREFIX + DB_OWNER, null, DB_MODE_WRITE));
        }
    }

    private void setUp(SQLiteDatabase sqLiteDatabase) {
        if (null == sqLiteDatabase) {
            throw new SQLiteException("数据库创建失败");
        }
        mDownloadSQLiteDatabase = sqLiteDatabase;
//		setWAL();
//		getPragma();
    }

    private SQLiteDatabase getDaoDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int mode) {
        SQLiteDatabase sqLiteDatabase;
        try {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, name, factory);
//			if (DB_MODE_READ == mode) {
//				sqLiteDatabase = helper.getReadableDatabase();
//			} else if (DB_MODE_WRITE == mode) {
            sqLiteDatabase = helper.getWritableDatabase();
//			} else {
//				return null;
//			}
        } catch (Exception e) {
            Log.e(TAG, "get getDaoDataBase error :"+e);
            return null;
        }

        return sqLiteDatabase;
    }

    private SQLiteDatabase getDownloadSQLiteDatabase() {
        if (mDownloadSQLiteDatabase == null) {
            setUp(getDaoDataBase(mContext,  DB_PREFIX + DB_OWNER, null, DB_MODE_WRITE));
        }
        return mDownloadSQLiteDatabase;
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        try {
            if (null != mDownloadSQLiteDatabase) {
                mDownloadSQLiteDatabase.close();
                mDownloadSQLiteDatabase = null;
            }
        } catch (Exception e) {
            mDownloadSQLiteDatabase = null;
        }
    }

    public ArrayList<DownloadInfo> queryAll() {
        getDownloadSQLiteDatabase();
        if(null == mDownloadSQLiteDatabase) {
            return null;
        }

        String querySql = "select * from " + DownloadInfoDao.TABLENAME;

        try {
            Cursor cursor = mDownloadSQLiteDatabase.rawQuery(querySql, null);
            if(null == cursor) {
                return null;
            }

            ArrayList<DownloadInfo> downloadInfos = new ArrayList<>();

            while (cursor.moveToNext()) {
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setId(cursor.getLong(DownloadInfoDao.Properties.Id.ordinal));
                downloadInfo.setAppName(cursor.getString(DownloadInfoDao.Properties.AppName.ordinal));
                downloadInfo.setSize(cursor.getString(DownloadInfoDao.Properties.Size.ordinal));
                downloadInfo.setDeepLink(cursor.getString(DownloadInfoDao.Properties.DeepLink.ordinal));
                downloadInfo.setPackageName(cursor.getString(DownloadInfoDao.Properties.PackageName.ordinal));
                downloadInfo.setUrl(cursor.getString(DownloadInfoDao.Properties.Url.ordinal));
                downloadInfo.setRealUrl(cursor.getString(DownloadInfoDao.Properties.RealUrl.ordinal));
                downloadInfo.setFileMd5(cursor.getString(DownloadInfoDao.Properties.FileMd5.ordinal));
                downloadInfo.setProgress(cursor.getLong(DownloadInfoDao.Properties.Progress.ordinal));
                downloadInfo.setTotalSize(cursor.getLong(DownloadInfoDao.Properties.TotalSize.ordinal));
                downloadInfo.setState(cursor.getInt(DownloadInfoDao.Properties.State.ordinal));
                downloadInfos.add(downloadInfo);
            }

            try {
                cursor.close();
            } catch (Exception e1){}

            return downloadInfos;
        } catch (Exception e) {
            return null;
        }
    }

    public DownloadInfo queryDownloadInfo(String packageName) {
        getDownloadSQLiteDatabase();
        if(null == mDownloadSQLiteDatabase) {
            return null;
        }

        String querySql = "select * from " + DownloadInfoDao.TABLENAME + " where "+ DownloadInfoDao.Properties.PackageName.columnName  + "='" + packageName +"'";

        try {
            Cursor cursor = mDownloadSQLiteDatabase.rawQuery(querySql, null);
            if(null == cursor) {
                return null;
            }

            cursor.moveToFirst();
            DownloadInfo downloadInfo = new DownloadInfo();
            downloadInfo.setId(cursor.getLong(DownloadInfoDao.Properties.Id.ordinal));
            downloadInfo.setAppName(cursor.getString(DownloadInfoDao.Properties.AppName.ordinal));
            downloadInfo.setSize(cursor.getString(DownloadInfoDao.Properties.Size.ordinal));
            downloadInfo.setDeepLink(cursor.getString(DownloadInfoDao.Properties.DeepLink.ordinal));
            downloadInfo.setPackageName(cursor.getString(DownloadInfoDao.Properties.PackageName.ordinal));
            downloadInfo.setUrl(cursor.getString(DownloadInfoDao.Properties.Url.ordinal));
            downloadInfo.setRealUrl(cursor.getString(DownloadInfoDao.Properties.RealUrl.ordinal));
            downloadInfo.setFileMd5(cursor.getString(DownloadInfoDao.Properties.FileMd5.ordinal));
            downloadInfo.setProgress(cursor.getLong(DownloadInfoDao.Properties.Progress.ordinal));
            downloadInfo.setTotalSize(cursor.getLong(DownloadInfoDao.Properties.TotalSize.ordinal));
            downloadInfo.setState(cursor.getInt(DownloadInfoDao.Properties.State.ordinal));

            try {
                cursor.close();
            } catch (Exception e1){}

            return downloadInfo;
        } catch (Exception e) {
            return null;
        }
    }

    public long insert(DownloadInfo downloadInfo) {
        getDownloadSQLiteDatabase();
        if(null == mDownloadSQLiteDatabase) {
            return -1;
        }

        try {
            long result;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadInfoDao.Properties.AppName.columnName, downloadInfo.getAppName());
            contentValues.put(DownloadInfoDao.Properties.Size.columnName, downloadInfo.getSize());
            contentValues.put(DownloadInfoDao.Properties.DeepLink.columnName, downloadInfo.getDeepLink());
            contentValues.put(DownloadInfoDao.Properties.PackageName.columnName, downloadInfo.getPackageName());
            contentValues.put(DownloadInfoDao.Properties.Url.columnName, downloadInfo.getUrl());
            contentValues.put(DownloadInfoDao.Properties.RealUrl.columnName, downloadInfo.getRealUrl());
            contentValues.put(DownloadInfoDao.Properties.FileMd5.columnName, downloadInfo.getFileMd5());
            contentValues.put(DownloadInfoDao.Properties.Progress.columnName, downloadInfo.getProgress());
            contentValues.put(DownloadInfoDao.Properties.TotalSize.columnName, downloadInfo.getTotalSize());
            contentValues.put(DownloadInfoDao.Properties.State.columnName, downloadInfo.getState());
            result = mDownloadSQLiteDatabase.insert(DownloadInfoDao.TABLENAME, null, contentValues);
            Log.d("cyTest", "insert-result:"+result);
            return result;
        } catch (Exception e) {
            return -1;
        }
    }

    public int update(DownloadInfo downloadInfo) {
        getDownloadSQLiteDatabase();
        if(null == mDownloadSQLiteDatabase) {
            return -1;
        }

        try {
            int result;
            ContentValues contentValues = new ContentValues();
            contentValues.put(DownloadInfoDao.Properties.AppName.columnName, downloadInfo.getAppName());
            contentValues.put(DownloadInfoDao.Properties.Size.columnName, downloadInfo.getSize());
            contentValues.put(DownloadInfoDao.Properties.DeepLink.columnName, downloadInfo.getDeepLink());
            contentValues.put(DownloadInfoDao.Properties.PackageName.columnName, downloadInfo.getPackageName());
            contentValues.put(DownloadInfoDao.Properties.Url.columnName, downloadInfo.getUrl());
            contentValues.put(DownloadInfoDao.Properties.RealUrl.columnName, downloadInfo.getRealUrl());
            contentValues.put(DownloadInfoDao.Properties.FileMd5.columnName, downloadInfo.getFileMd5());
            contentValues.put(DownloadInfoDao.Properties.Progress.columnName, downloadInfo.getProgress());
            contentValues.put(DownloadInfoDao.Properties.TotalSize.columnName, downloadInfo.getTotalSize());
            contentValues.put(DownloadInfoDao.Properties.State.columnName, downloadInfo.getState());
            result = mDownloadSQLiteDatabase.update(DownloadInfoDao.TABLENAME, contentValues, DownloadInfoDao.Properties.Id.columnName + "=?", new String[]{String.valueOf(downloadInfo.getId())});

            Log.d("cyTest", "update-result:"+result);
            return result;
        } catch (Exception e) {
            return -1;
        }
    }

    public long delete(DownloadInfo downloadInfo) {
        if(null == downloadInfo || downloadInfo.getId() == -1) {
            return -1;
        }

        try {
            getDownloadSQLiteDatabase();
            if(null == mDownloadSQLiteDatabase) {
                return -1;
            }

//            String delStr = "DELETE FROM " + DownloadInfoDao.TABLENAME + " where "+DownloadInfoDao.Properties.Id.columnName +"="+downloadInfo.getId();
            int result = mDownloadSQLiteDatabase.delete(DownloadInfoDao.TABLENAME, DownloadInfoDao.Properties.Id.columnName + "=?", new String[]{String.valueOf(downloadInfo.getId())});
            Log.d("cyTest", "delete--result:"+result);
//            mDownloadSQLiteDatabase.execSQL(delStr);
            return result;
        } catch (Exception e) {
            Log.w("cyTest", "deleteInterceptData error:"+e.getMessage());
        }

        return -1;
    }
}
