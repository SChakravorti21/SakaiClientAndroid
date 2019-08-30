package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.os.Bundle;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by Shoumyo Chakravorti on 6/30/18.
 *
 * The {@link RecyclerView.Adapter} that instantiates
 * instances of {@link SingleAssignmentFragment} to populate the
 * {@link ViewPager} of a
 * {@link SiteAssignmentsFragment}.
 */
public class SiteAssignmentPagerAdapter extends FragmentStatePagerAdapter {

    /**
     * The list of {@link Assignment}s for the current course being viewed.
     */
    private List<Assignment> assignments;

    /**
     * Calls the base constructor and also instantiates the {@code assignments} field.
     * @param fragmentManager the {@link FragmentManager} that will handle creation of
     *                        {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment}s.
     * @param assignments the list of {@link Assignment} objects for this course
     */
    public SiteAssignmentPagerAdapter(FragmentManager fragmentManager,
                                      List<Assignment> assignments) {
        super(fragmentManager);
        this.assignments = assignments;
    }

    /**
     * Returns a {@link Fragment} for the {@link Assignment} at the current position.
     * @param position the index of the {@link Assignment} in {@code assignments}.
     * @return a {@link Fragment} for this {@link Assignment}.
     */
    @Override
    public Fragment getItem(int position) {
        // Put the assignments in the arguments and instantiate the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(SingleAssignmentFragment.ASSIGNMENT_TAG, assignments.get(position));

        SingleAssignmentFragment fragment = new SingleAssignmentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * Returns the number of items (assignments) in this course.
     * @return the number of items
     */
    @Override
    public int getCount() {
        return (assignments != null) ? assignments.size() : 0;
    }
}
