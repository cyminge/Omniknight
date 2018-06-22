package com.cy.omniknight.verify.mustbe;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cy on 18-6-1.
 */

public class MustBeTest {

    public static void main(String[] args) {
        Target target = new Target();
        target.setParams(1);
    }

}

class Target {

    @IntDef({PARAMS_1, PARAMS_2})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PARAM {}

    static final int PARAMS_1 = 1;
    static final int PARAMS_2 = 2;

    int mParam;

    public void setParams(@PARAM int param) {
        mParam = param;
    }
}
