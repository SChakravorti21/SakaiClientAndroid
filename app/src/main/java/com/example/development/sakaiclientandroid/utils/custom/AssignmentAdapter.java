package com.example.development.sakaiclientandroid.utils.custom;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
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

    public class ViewHolder extends RecyclerView.ViewHolder
            implements PopupMenu.OnMenuItemClickListener, View.OnClickListener {

        TextView titleView;
        TextView descriptionView;
        TextView dueDateView;
        int position;

        ViewHolder(CardView cardView) {
            super(cardView);
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);

            // Set the click listener on the header and footer
            titleView.setOnClickListener(this);
            dueDateView.setOnClickListener(this);
        }

        void setPosition(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int itemId = item.getItemId();

            switch (itemId) {
                case R.id.action_expand:
                    // This is the same behavior as expanding the card, so just
                    // trigger onClick.
                    // The view passed into the onClick listener does not matter
                    // much since it is only used to get the context for using the
                    // fragment manager.
                    this.expandAssignment( (AppCompatActivity) titleView.getContext() );
                    return true;
            }

            return false;
        }

        @Override
        public void onClick(View v) {
            this.expandAssignment( (AppCompatActivity) v.getContext() );
        }

        private void expandAssignment(AppCompatActivity activity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(ASSIGNMENTS_TAG, (Serializable) assignments);
            bundle.putInt(ASSIGNMENT_NUMBER, position);

            CourseAssignmentsFragment fragment = new CourseAssignmentsFragment();
            fragment.setArguments(bundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                fragment.setEnterTransition(new Fade());
                fragment.setExitTransition(new Fade());
            }

            activity.getSupportFragmentManager()
                    .beginTransaction()
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
        CardView view = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_cell_layout, parent, false);

        // Inflate the options menu (mainly just used for expanding the card)
        TextView popupView = view.findViewById(R.id.assignment_popup_menu);
        final PopupMenu popupMenu = new PopupMenu(parent.getContext(), popupView);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.assignment_card_menu, popupMenu.getMenu());

        // Set the listener for menu click
        ViewHolder holder = new ViewHolder(view);
        popupMenu.setOnMenuItemClickListener(holder);

        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        return holder;
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
        viewHolder.dueDateView.setText("Due: " + assignment.getDueTime().getDisplay());
    }

    // Return the size of your data ser (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return assignments.size();
    }

}
