package com.example.development.sakaiclientandroid.utils.custom;

import android.content.Context;
import android.widget.ExpandableListView;

public class CustomParentListView extends ExpandableListView {

    public CustomParentListView(Context context) {
        super(context);
    }

    //if the measure specs are not increased, the 3rd level children items dont show
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(20000, MeasureSpec.AT_MOST);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(20000, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
