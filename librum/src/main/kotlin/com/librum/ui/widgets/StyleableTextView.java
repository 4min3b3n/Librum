package com.librum.ui.widgets;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;


public class StyleableTextView extends AppCompatTextView {

  public StyleableTextView(Context context) {
    super(context);
  }

  public StyleableTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
//        UiUtil.setCustomFont(this, context, attrs, R.styleable.StyleableTextView, R.styleable.StyleableTextView_font);
  }

  public StyleableTextView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
//        UiUtil.setCustomFont(this, context, attrs, R.styleable.StyleableTextView,
//                R.styleable.StyleableTextView_font);
  }

}
