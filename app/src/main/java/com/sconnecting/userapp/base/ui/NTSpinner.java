package com.sconnecting.userapp.base.ui;

import android.content.Context;
import android.util.AttributeSet;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * Created by TrungDao on 12/8/16.
 */

public class NTSpinner extends MaterialBetterSpinner {

    public NTSpinner(Context context) {
        super(context);
    }

    public NTSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public NTSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }


    @Override
    protected void performFiltering(CharSequence text, int keyCode) {

    }

}
