package com.example.development.sakaiclientandroid.utils.custom;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.fragments.WebFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Development on 6/23/18.
 */

public class AssignmentAdapter extends RecyclerView.Adapter {

    private List<AssignmentObject> assignments;
    private static boolean setFragmentManager;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView titleView;
        TextView descriptionView;
        TextView dueDateView;

        ViewHolder(CardView cardView) {
            super(cardView);
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);

            // Set the click listener on the entire CardView
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NavActivity activity = (NavActivity) v.getContext();
            Log.d("CardView", "has been clicked");

            Bundle bundle = new Bundle();
            bundle.putSerializable(NavActivity.ASSIGNMENTS_TAG, (Serializable) assignments);

            WebFragment fragment = new WebFragment();
            fragment.setArguments(bundle);

            FragmentManager manager = activity.getSupportFragmentManager();
            manager.beginTransaction()
                    .setCustomAnimations(R.anim.enter,
                            R.anim.exit,
                            R.anim.pop_enter,
                            R.anim.pop_exit)
                    // Add instead of replacing so that the state of opened assignments
                    // remains the same after returning
                    .add(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
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

}
