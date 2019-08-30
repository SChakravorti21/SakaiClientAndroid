package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage;
import com.sakaimobile.development.sakaiclient20.ui.activities.SitePageActivity;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TreeSitePageAdapter extends RecyclerView.Adapter<TreeSitePageAdapter.SitePageViewHolder> {

    private static final String CHEVRON_RIGHT = "\uF105";
    private static final Map<String, String> sitePageIcons = new HashMap<String, String>() {{
        put(SitePageActivity.GRADEBOOK, "\uF46C");
        put(SitePageActivity.ANNOUNCEMENTS, "\uF0F3");
        put(SitePageActivity.RESOURCES, "\uF019");
        put(SitePageActivity.ASSIGNMENTS, "\uF044");
        put(SitePageActivity.CHAT_ROOM, "\uF086");
    }};

    private Course course;

    public TreeSitePageAdapter(@NonNull Course course) {
        this.course = course;
    }

    @NonNull
    @Override
    public SitePageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new SitePageViewHolder(inflater.inflate(R.layout.tree_node_site_page, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SitePageViewHolder sitePageViewHolder, int position) {
        SitePage sitePage = this.course.sitePages.get(position);
        sitePageViewHolder.sitePageTitle.setText(sitePage.title);
        sitePageViewHolder.arrowImage.setText(CHEVRON_RIGHT);

        // Only set the icon text if there is a match, be sure to clear it otherwise
        // since views are recycled
        if(sitePageIcons.containsKey(sitePage.title))
            sitePageViewHolder.sitePageIcon.setText(sitePageIcons.get(sitePage.title));
        else
            sitePageViewHolder.sitePageIcon.setText("");
    }

    @Override
    public int getItemCount() {
        return this.course.sitePages != null ? this.course.sitePages.size() : 0;
    }

    class SitePageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView sitePageTitle;
        TextView sitePageIcon;
        TextView arrowImage;

        SitePageViewHolder(@NonNull View itemView) {
            super(itemView);
            this.sitePageTitle = itemView.findViewById(R.id.site_page_title);
            this.sitePageIcon = itemView.findViewById(R.id.site_page_icon);
            this.arrowImage = itemView.findViewById(R.id.arrow_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();
            // Start the SitePageActivity to handle the appropriate site page
            Intent sitePageIntent = new Intent(context, SitePageActivity.class);
            sitePageIntent.putExtra(context.getString(R.string.site_type_tag), sitePageTitle.getText());
            sitePageIntent.putExtra(context.getString(R.string.course_tag), course);

            context.startActivity(sitePageIntent);
        }
    }
}
