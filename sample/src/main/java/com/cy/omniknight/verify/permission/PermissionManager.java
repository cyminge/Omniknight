package com.cy.omniknight.verify.permission;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PermissionManager {
    public static final String REQUEST_CODE = "requestCode";
    public static final String PERMISSIONS = "permissions";
    private static final int VERSION_CODES_ANDROID_M = 23;

    private ArrayList<PermissionCallBack> mCallBackList = new ArrayList<PermissionCallBack>();

    public static final int M = 23;

    private final static class PermissionManagerHolder {
        final public static PermissionManager sInstance = new PermissionManager();
    }

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        return PermissionManagerHolder.sInstance;
    }

    public void requestPermissions(Context context, PermissionCallBack callBack, int requestCode, String... perms) {
        mCallBackList.add(callBack);
        Intent intent = new Intent(context, AssistActivity.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(PERMISSIONS, perms);
        context.startActivity(intent);
    }

    void requestPermissions(Fragment fragment, int requestCode, String... perms) {
        if (Build.VERSION.SDK_INT < M) {
            if (fragment instanceof PermissionCallBack) {
                ArrayList<String> permissionList = new ArrayList<String>();
                for (String permission : perms) {
                    permissionList.add(permission);
                }
                for (PermissionCallBack callBack : mCallBackList) {
                    callBack.onPermissionsGranted(requestCode, permissionList);
                }
            }

            return;
        }
        ArrayList<String> permission = getCanRequestPermission(fragment, requestCode, perms);
        //fragment.requestPermissions(permission.toArray(new String[permission.size()]), requestCode);
        Class<?>[] parameterTypes = new Class<?>[]{String[].class, int.class};
        Object[] args = new Object[]{permission.toArray(new String[permission.size()]), requestCode};
        try {
            getMethod(Fragment.class.getName(), fragment, "requestPermissions", parameterTypes, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean shouldShowRequestPermissionRationale(Fragment fragment, String permission) {
        if (!isAndroidM()) {
            return true;
        }
        //return activity.shouldShowRequestPermissionRationale(permission);
        Class<?>[] parameterTypes = new Class<?>[]{String.class};
        Object[] args = new Object[]{permission};
        try {
            return (Boolean) getMethod(Fragment.class.getName(), fragment, "shouldShowRequestPermissionRationale", parameterTypes, args);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkSelfPermissionGranted(Context context, String permission) {
        if (!isAndroidM()) {
            return true;
        }
        int result = checkSelfPermission(context, permission);
        return PackageManager.PERMISSION_GRANTED == result;
    }

    private int checkSelfPermission(Context context, String permission) {
        Class<?>[] parameterTypes = new Class<?>[]{String.class};
        Object[] args = new Object[]{permission};
        try {
            return (Integer) (getMethod(Context.class.getName(), context, "checkSelfPermission", parameterTypes, args));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static Object getMethod(String className, Object instance, String methodName,
                                    Class<?>[] parameterTypes, Object[] args) throws Exception {
        Class classes = Class.forName(className);
        if (instance == null) {
            throw new Exception("-----------反射获取类实例:" + className + "失败，返回");
        } else {
            Method method = classes.getMethod(methodName, parameterTypes);
            Object roProductManufacturer = method.invoke(instance, args);
            return roProductManufacturer;
        }
    }

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        ArrayList<String> granted = new ArrayList<String>();
        ArrayList<String> denied = new ArrayList<String>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        if (denied.size() > 0) {
            for (PermissionCallBack callBack : mCallBackList) {
                callBack.onPermissionsDenied(requestCode, denied);
            }
        }
        if (granted.size() > 0) {
            for (PermissionCallBack callBack : mCallBackList) {
                callBack.onPermissionsGranted(requestCode, granted);
            }
        }
        mCallBackList.clear();
    }

    private ArrayList<String> getCanRequestPermission(Fragment fragment, int requestCode, String... permissions) {
        ArrayList<String> unAchievedPermission = new ArrayList<String>();
        ArrayList<String> achievedPermission = new ArrayList<String>();
        for (String permission : permissions) {
            if (checkSelfPermissionGranted(fragment.getActivity(), permission)) {
                achievedPermission.add(permission);
            } else {
                unAchievedPermission.add(permission);
            }
        }
        if (achievedPermission.size() > 0) {
            for (PermissionCallBack callBack : mCallBackList) {
                callBack.onPermissionsGranted(requestCode, achievedPermission);
            }
        }

        ArrayList<String> permissionList = new ArrayList<String>();
        ArrayList<String> denyPermissionList = new ArrayList<String>();
        for (String permission : unAchievedPermission) {
            if (shouldShowRequestPermissionRationale(fragment, permission)) {
                permissionList.add(permission);
                continue;
            }
            denyPermissionList.add(permission);
        }
        if (denyPermissionList.size() > 0) {
            if (permissionList.size() == 0) {
                return denyPermissionList;
            } else {
                for (PermissionCallBack callBack : mCallBackList) {
                    callBack.onPermissionsDenied(requestCode, denyPermissionList);
                }
            }
        }
        return permissionList;
    }

    private static boolean isAndroidM() {
        return Build.VERSION.SDK_INT >= VERSION_CODES_ANDROID_M;
    }
}
