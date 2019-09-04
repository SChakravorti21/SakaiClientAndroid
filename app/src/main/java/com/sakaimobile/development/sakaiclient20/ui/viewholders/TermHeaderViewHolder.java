package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.unnamed.b.atv.model.TreeNode;

import androidx.appcompat.widget.LinearLayoutCompat;

/**
 * Created by Development on 6/9/18.
 */

public class TermHeaderViewHolder extends
        TreeNode.BaseNodeViewHolder<TermHeaderViewHolder.TermHeaderItem> {

    private static final String CHEVRON_DOWN = "\uF107";
    private static final String CHEVRON_RIGHT = "\uF105";

    private TextView arrowView;

    public TermHeaderViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, TermHeaderItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_term_header, null, false);

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
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ));

        // If this is the last term, do not show the bottom border (looks weird)
        if(node.isLastChild())
            view.setBackgroundResource(R.color.secondaryBackgroundColor);

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
