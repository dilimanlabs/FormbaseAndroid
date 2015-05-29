package com.dilimanlabs.formbase.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.TextView;

import com.dilimanlabs.formbase.R;

/**
 * Created by user on 5/29/2015.
 */
public class CategoryCardView extends CardView {

    private TextView categoryName;
    private TextView formName;

    public CategoryCardView(Context context) {
        super(context);
        init();
    }

    public CategoryCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CategoryCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.view_category_card, this);
        categoryName = (TextView) findViewById(R.id.categoryName);
        formName = (TextView) findViewById(R.id.formNumber);
    }

    public TextView getCategoryName() {
        return categoryName;
    }

    public TextView getFormName() {
        return formName;
    }
}
