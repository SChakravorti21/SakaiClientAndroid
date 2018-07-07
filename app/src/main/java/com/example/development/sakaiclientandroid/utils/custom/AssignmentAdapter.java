package com.example.development.sakaiclientandroid.utils.custom;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.fragments.WebFragment;
import com.example.development.sakaiclientandroid.fragments.assignments.AssignmentTransition;
import com.example.development.sakaiclientandroid.fragments.assignments.CourseAssignmentsFragment;

import java.io.Serializable;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;
import static com.example.development.sakaiclientandroid.fragments.assignments.CourseAssignmentsFragment.ASSIGNMENT_NUMBER;

/**
 * Created by Development on 6/23/18.
 */

public class AssignmentAdapter extends RecyclerView.Adapter {

    private List<AssignmentObject> assignments;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView titleView;
        TextView descriptionView;
        TextView dueDateView;
        int position;

        ViewHolder(CardView cardView) {
            super(cardView);
            this.cardView = cardView;
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);
        }

        void setPosition(int position) {
            this.position = position;

            // Set the click listener on the entire CardView
            CardClickListener listener = new CardClickListener(position);
            this.cardView.setOnClickListener(listener);
        }
    }

    public AssignmentAdapter(List<AssignmentObject> assignments) {
        this.assignments = assignments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view. The entire CardView must be passed into
        // the ViewHolder constructor since it is the parent of all inner elements.
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_cell_layout, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element
        ViewHolder viewHolder = (ViewHolder) holder;
        // Update the ViewHolder's position so that the click
        // listener can dictate how to initialize the CourseAssignmentsFragment
        viewHolder.setPosition(position);

        // Set the assignment title
        AssignmentObject assignment = assignments.get(position);
        viewHolder.titleView.setText(assignment.getTitle());

        // Set the assignment description
        // fromHtml(String) was deprecated in android N, so check the build version
        //before converting the html to text
        String instructions = assignment.getInstructions();
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions);
        };
        viewHolder.descriptionView.setText(description);
        viewHolder.descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());

        // Set the assignment due date
        viewHolder.dueDateView.setText("Due: " + assignment.getDueTimeString());
    }

    // Return the size of your data ser (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return assignments.size();
    }


    private class CardClickListener implements View.OnClickListener {
        private int position;

        /**
         * The constructor takes in an integer parameter to indicate what the
         * position of the assignment is. This allows it to automatically go to
         * that assignment in the full views when it is clicked.
         * @param position
         */
        CardClickListener(int position) {
            this.position = position;
        }

        /**
         * Creates and adds a new fragment to the view stack which represents
         * all of the class's assignments. The initial assignment is set to
         * the one that was clicked.
         * @param v
         */
        @Override
        public void onClick(View v) {
            NavActivity activity = (NavActivity) v.getContext();

            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) assignments);
            bundle.putInt(ASSIGNMENT_NUMBER, position);

            CourseAssignmentsFragment fragment = new CourseAssignmentsFragment();
            fragment.setArguments(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                fragment.setEnterTransition(new Fade());
                fragment.setExitTransition(new Fade());
            }

            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction()
                    // Add instead of replacing so that the state of opened assignments
                    // remains the same after returning
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
