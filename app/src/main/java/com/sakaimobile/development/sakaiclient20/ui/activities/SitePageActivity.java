package com.sakaimobile.development.sakaiclient20.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import com.sakaimobile.development.sakaiclient20.R;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.persistence.entities.Course;
import com.sakaimobile.development.sakaiclient20.persistence.entities.SitePage;
import com.sakaimobile.development.sakaiclient20.ui.custom_components.CustomLinkMovementMethod;
import com.sakaimobile.development.sakaiclient20.ui.fragments.AnnouncementsFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteChatFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteGradesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.SiteResourcesFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.WebFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;


public class SitePageActivity extends AppCompatActivity {

    public static final String GRADEBOOK = "Gradebook";
    public static final String ANNOUNCEMENTS = "Announcements";
    public static final String RESOURCES = "Resources";
    public static final String ASSIGNMENTS = "Assignments";
    public static final String CHAT_ROOM = "Chat Room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_page);

        Intent intent = getIntent();
        String siteType = intent.getStringExtra(getString(R.string.site_type_tag));
        Course course = (Course) intent.getSerializableExtra(getString(R.string.course_tag));

        // setup toolbar, enable returning to parent activity
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setTitle(siteType);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        // load the appropriate fragment for the site type
        if (siteType.equals(GRADEBOOK)) {
            startSiteGradesFragment(course);
        } else if (siteType.equals(ANNOUNCEMENTS)) {
            startSiteAnnouncementsFragment(course);
        } else if (siteType.equals(RESOURCES)) {
            startSiteResourcesFragment(course);
        } else if (siteType.equals(ASSIGNMENTS)) {
            if(course != null)
                // If the course is not null, show assignment just for that course's site ID
                startSiteAssignmentsFragment(course, null, 0);
            else {
                List<Assignment> assignments = (List<Assignment>) intent.getSerializableExtra(getString(R.string.assignments_tag));
                int initialPosition = intent.getIntExtra(SiteAssignmentsFragment.INITIAL_VIEW_POSITION, 0);
                startSiteAssignmentsFragment(null, assignments, initialPosition);
            }
        } else if (siteType.equals(CHAT_ROOM)) {
            startChatRoomFragment(course);
        } else {
            startWebViewFragment(siteType, course);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomLinkMovementMethod.setFragmentManager(getSupportFragmentManager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to returning to parent activity
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSiteGradesFragment(Course course) {
        Bundle bun = new Bundle();
        bun.putString(getString(R.string.siteid_tag), course.siteId);

        SiteGradesFragment fragment = new SiteGradesFragment();
        fragment.setArguments(bun);

        addFragment(fragment);
    }

    private void startSiteAnnouncementsFragment(Course course) {
        Bundle bun = new Bundle();
        bun.putString(getString(R.string.siteid_tag), course.siteId);

        AnnouncementsFragment fragment = new AnnouncementsFragment();
        fragment.setArguments(bun);

        addFragment(fragment);
    }

    private void startSiteResourcesFragment(Course course) {
        Bundle bun = new Bundle();
        bun.putString(getString(R.string.siteid_tag), course.siteId);

        SiteResourcesFragment fragment = new SiteResourcesFragment();
        fragment.setArguments(bun);

        addFragment(fragment);
    }

    private void startSiteAssignmentsFragment(Course course, List<Assignment> assignments, int initialPosition) {
        // The SiteAssignmentsFragment expects a Map that ties site IDs to the
        // assignment site page URL, so let's construct that map
        Map<String, String> mapSiteIdToSitePageUrl = new HashMap<>();

        if(course != null) {
            // If the course is not null, then we only need to show assignments for one
            // site ID
            mapSiteIdToSitePageUrl.put(course.siteId, course.assignmentSitePageUrl);
        } else {
            for (Assignment assignment : assignments)
                mapSiteIdToSitePageUrl.put(assignment.siteId, assignment.assignmentSitePageUrl);
        }

        Bundle bun = new Bundle();
        bun.putSerializable(SiteAssignmentsFragment.SITE_IDS_TAG, (Serializable) mapSiteIdToSitePageUrl);
        bun.putInt(SiteAssignmentsFragment.INITIAL_VIEW_POSITION, initialPosition);

        SiteAssignmentsFragment fragment = new SiteAssignmentsFragment();
        fragment.setArguments(bun);

        addFragment(fragment);
    }

    private void startChatRoomFragment(Course course) {
        // Our chat fragment uses WebView#evaluateJavascript()
        // to get the chat channel ID and CSRF token, and this method
        // is restricted to API >= 19
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startWebViewFragment(CHAT_ROOM, course);
            return;
        }

        // Get the URL that needs to be shown in the char fragment
        // (get the `url` property off of the chat site page)
        String chatSitePageUrl = null;
        for(SitePage sitePage : course.sitePages) {
            if(sitePage.title.equals(CHAT_ROOM)) {
                chatSitePageUrl = sitePage.url;
                break;
            }
        }

        if(chatSitePageUrl == null)
            throw new RuntimeException("Chat site page url cannot be null");

        Bundle bun = new Bundle();
        bun.putString(SiteChatFragment.CHAT_SITE_URL, chatSitePageUrl);

        SiteChatFragment fragment = new SiteChatFragment();
        fragment.setArguments(bun);

        addFragment(fragment);
    }

    private void startWebViewFragment(String siteName, Course course) {
        String url = null;
        for (SitePage page : course.sitePages) {
            if (page.title.equals(siteName))
                url = page.url;
        }

        // url will never be null because we only displayed
        // site pages with valid urls, so the user can never click
        // on a site page with an invalid url
        WebFragment fragment = WebFragment.newInstance(url);
        addFragment(fragment);
    }


    private void addFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }


}
