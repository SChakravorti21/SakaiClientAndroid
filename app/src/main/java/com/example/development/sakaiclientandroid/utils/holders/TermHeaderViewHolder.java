package com.example.development.sakaiclientandroid.utils.holders;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Development on 6/9/18.
 */

public class TermHeaderViewHolder extends
        TreeNode.BaseNodeViewHolder<TermHeaderViewHolder.TermHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF078";
    private static final String CHEVRON_RIGHT = "\uF054";

    public TermHeaderViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, TermHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.term_header_layout, null, false);

        TextView tvValue = view.findViewById(R.id.term_name);
        tvValue.setText(value.text);

        Resources r = inflater.getContext().getResources();
        int widthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDisplayMetrics().widthPixels,
                r.getDisplayMetrics()
        );

        LinearLayoutCompat.LayoutParams params =  new LinearLayoutCompat.LayoutParams(
            widthPx,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );

        view.setLayoutParams(params);

        return view;
    }

    public static class TermHeaderItem {
        public String text;

        public TermHeaderItem(String text) {
            this.text = text;
        }
    }


}
