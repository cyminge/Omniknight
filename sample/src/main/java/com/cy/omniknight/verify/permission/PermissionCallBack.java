package com.cy.omniknight.verify.permission;

import java.util.List;


public interface PermissionCallBack {
    void onPermissionsGranted(int requestCode, List<String> perms);

    void onPermissionsDenied(int requestCode, List<String> perms);
}
