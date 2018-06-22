package com.cy.omniknight.verify.permission;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;

import static com.cy.omniknight.verify.permission.PermissionManager.PERMISSIONS;
import static com.cy.omniknight.verify.permission.PermissionManager.REQUEST_CODE;

public class AssistFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions();
    }

    private void requestPermissions() {
        Intent intent = getActivity().getIntent();
        final int requestCode = intent.getIntExtra(REQUEST_CODE, 0);
        final String[] permissions = intent.getStringArrayExtra(PERMISSIONS);
        new Thread(new Runnable() {
            @Override
            public void run() {
                PermissionManager.getInstance().requestPermissions(AssistFragment.this, requestCode, permissions);
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);

        getActivity().finish();
    }

}
