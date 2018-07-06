package com.example.development.sakaiclientandroid.fragments.assignments;

import android.support.transition.ChangeBounds;
import android.support.transition.ChangeImageTransform;
import android.support.transition.ChangeTransform;
import android.support.transition.TransitionSet;

/**
 * Created by Development on 7/6/18.
 */

public class AssignmentTransition extends TransitionSet {

    public AssignmentTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds())
                .addTransition(new ChangeTransform())
                // TODO: Check if this one is necessary
                .addTransition(new ChangeImageTransform());
    }
}
