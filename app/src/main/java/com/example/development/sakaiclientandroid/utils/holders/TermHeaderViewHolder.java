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

    private TextView arrowView;

    public TermHeaderViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, TermHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.term_header_layout, null, false);

        // Set the term text
        TextView tvValue = view.findViewById(R.id.term_name);
        tvValue.setText(value.text);

        // Initialize the arrow view for toggling the list
        arrowView = view.findViewById(R.id.arrow_image);
        if (node.getLevel() < 4) {
            arrowView.setText(CHEVRON_RIGHT);
        } else {
            arrowView.setVisibility(View.GONE);
            arrowView = null;
        }

        // Set the header size to the screen width for a full-bleed effect
        Resources r = inflater.getContext().getResources();
        int widthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDisplayMetrics().widthPixels,
                r.getDisplayMetrics()
        );

        // Apply the layout parameters
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void toggle(boolean active) {
        if (arrowView != null)
            arrowView.setText(active ? CHEVRON_DOWN : CHEVRON_RIGHT);
    }

    public static class TermHeaderItem {
        public String text;

        public TermHeaderItem(String text) {
            this.text = text;
        }
    }


}
