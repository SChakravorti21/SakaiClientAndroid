package com.sakaimobile.development.sakaiclient20.ui.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sakaimobile.development.sakaiclient20.persistence.entities.Assignment;
import com.sakaimobile.development.sakaiclient20.ui.activities.MainActivity;
import com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment;

import java.util.List;

/**
 * Created by Shoumyo Chakravorti on 6/30/18.
 *
 * The {@link android.support.v7.widget.RecyclerView.Adapter} that instantiates
 * instances of {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SingleAssignmentFragment} to populate the
 * {@link android.support.v4.view.ViewPager} of a
 * {@link com.sakaimobile.development.sakaiclient20.ui.fragments.assignments.SiteAssignmentsFragment}.
 */
public class AssignmentsPagerAdapter extends FragmentStatePagerAdapter {

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
    public AssignmentsPagerAdapter(FragmentManager fragmentManager,
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
        bundle.putSerializable(MainActivity.ASSIGNMENTS_TAG, assignments.get(position));

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
