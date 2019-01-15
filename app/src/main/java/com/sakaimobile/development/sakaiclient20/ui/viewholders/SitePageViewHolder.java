package com.sakaimobile.development.sakaiclient20.ui.viewholders;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.ui.activities.SitePageActivity;
import com.unnamed.b.atv.model.TreeNode;

import java.util.HashMap;
import java.util.Map;

public class SitePageViewHolder extends TreeNode.BaseNodeViewHolder<SitePageViewHolder.SitePageItem> implements View.OnClickListener {

    private static final Map<String, String> sitePageIcons = new HashMap<String, String>() {{
        put(SitePageActivity.GRADEBOOK, "\uF46C");
        put(SitePageActivity.ANNOUNCEMENTS, "\uF0F3");
        put(SitePageActivity.RESOURCES, "\uF019");
        put(SitePageActivity.ASSIGNMENTS, "\uF044");
        put(SitePageActivity.CHAT_ROOM, "\uF086");
    }};

    private static final String CHEVRON_RIGHT = "\uF105";
    private SitePageItem sitePageItem;

    public SitePageViewHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, SitePageItem value) {
        this.sitePageItem = value;

        final LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.tree_node_site_page, null, false);

        // Set up site title and the arrow
        ((TextView) view.findViewById(R.id.site_page_title)).setText(value.sitePageTitle);
        ((TextView) view.findViewById(R.id.arrow_image)).setText(CHEVRON_RIGHT);

        if(value.sitePageIcon != null)
            ((TextView) view.findViewById(R.id.site_page_icon)).setText(value.sitePageIcon);

        Resources r = inflater.getContext().getResources();
        int widthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                r.getDisplayMetrics().widthPixels,
                r.getDisplayMetrics()
        );

        view.setOnClickListener(this);
        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(
                widthPx,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        ));

        return view;
    }

    @Override
    public void onClick(View v) {
        // We basically treat toggle as onClick here since there isn't a dedicated onClick method
        // Start the SitePageActivity to handle the appropriate site page
        Intent sitePageIntent = new Intent(context, SitePageActivity.class);
        sitePageIntent.putExtra(context.getString(R.string.site_type_tag), sitePageItem.sitePageTitle);
        sitePageIntent.putExtra(context.getString(R.string.course_tag), sitePageItem.course);

        context.startActivity(sitePageIntent);
    }

    public static class SitePageItem {
        String sitePageTitle;
        String sitePageIcon;
        Course course;

        public SitePageItem(String sitePageTitle, Course course) {
            this.course = course;
            this.sitePageTitle = sitePageTitle;
            this.sitePageIcon = sitePageIcons.get(sitePageTitle);
        }
    }
}
