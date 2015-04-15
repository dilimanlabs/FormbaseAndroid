package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 4/13/2015.
 */
public class CategoryView extends LinearLayout {
    LinearLayout linearLayoutCategory;
    public CategoryView(Context context) {
        super(context);
        init();
    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CategoryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_category, this);
        linearLayoutCategory = (LinearLayout) findViewById(R.id.layoutCategories);
    }
}
