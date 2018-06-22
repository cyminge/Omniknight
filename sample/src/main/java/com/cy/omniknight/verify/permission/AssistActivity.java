package com.cy.omniknight.verify.permission;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.FrameLayout;

public class AssistActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FrameLayout(this));

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .add(new AssistFragment(), AssistFragment.class.getSimpleName())
                .commit();
    }

}
