package com.example.development.sakaiclientandroid.utils.custom;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.Assignment;
import com.example.development.sakaiclientandroid.fragments.assignments.SiteAssignmentsFragment;

import java.io.Serializable;
import java.util.List;

import static com.example.development.sakaiclientandroid.NavActivity.ASSIGNMENTS_TAG;
import static com.example.development.sakaiclientandroid.fragments.assignments.SiteAssignmentsFragment.ASSIGNMENT_NUMBER;

/**
 * Created by Development on 6/23/18.
 */

public class AssignmentAdapter extends RecyclerView.Adapter {

    private List<Assignment> assignments;

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        TextView titleView;
        TextView descriptionView;
        TextView dueDateView;
        int position;

        ViewHolder(CardView cardView, View popupView) {
            super(cardView);
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);

            // Set the click listener on the header and footer
            cardView.setOnClickListener(this);
            titleView.setOnClickListener(this);
            descriptionView.setOnClickListener(this);
            dueDateView.setOnClickListener(this);
            popupView.setOnClickListener(this);
        }

        void setPosition(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            this.expandAssignment( (AppCompatActivity) v.getContext() );
        }

        private void expandAssignment(AppCompatActivity activity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) assignments);
            bundle.putInt(ASSIGNMENT_NUMBER, position);

            SiteAssignmentsFragment fragment = new SiteAssignmentsFragment();
            fragment.setArguments(bundle);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.anim_fade_in,
                            R.anim.anim_fade_out,
                            R.anim.anim_fade_in,
                            R.anim.anim_fade_out)
                    // Add instead of replacing so that the state of opened assignments
                    // remains the same after returning
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public AssignmentAdapter(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view. The entire CardView must be passed into
        // the ViewHolder constructor since it is the parent of all inner elements.
        CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_cell_layout, parent, false);

        // Inflate the options menu (mainly just used for expanding the card)
        TextView popupView = view.findViewById(R.id.assignment_expand_button);
        return new ViewHolder(view, popupView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element
        ViewHolder viewHolder = (ViewHolder) holder;
        // Update the ViewHolder's position so that the click
        // listener can dictate how to initialize the SiteAssignmentsFragment
        viewHolder.setPosition(position);

        // Set the assignment title
        Assignment assignment = assignments.get(position);
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
        viewHolder.dueDateView.setText("Due: " + assignment.getDueTime().getDisplay());
    }

    // Return the size of your data ser (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return assignments.size();
    }

}
