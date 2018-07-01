package com.example.development.sakaiclientandroid.utils.custom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.fragments.assignments.SingleAssignmentFragment;

import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;

/**
 * Created by Development on 6/30/18.
 */

public class AssignmentsPagerAdapter extends FragmentStatePagerAdapter {

    private List<AssignmentObject> assignments;

    public AssignmentsPagerAdapter(FragmentManager fragmentManager,
                                   List<AssignmentObject> assignments) {
        super(fragmentManager);
        this.assignments = assignments;
    }

    @Override
    public Fragment getItem(int position) {
        // Put the assignments in the arguments and instantiate the fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable(ASSIGNMENTS_TAG, assignments.get(position));

        SingleAssignmentFragment fragment = new SingleAssignmentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return (assignments != null) ? assignments.size() : 0;
    }
}
