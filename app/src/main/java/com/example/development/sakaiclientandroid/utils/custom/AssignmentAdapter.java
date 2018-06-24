package com.example.development.sakaiclientandroid.utils.custom;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.NavActivity;
import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.assignments.AssignmentObject;
import com.example.development.sakaiclientandroid.fragments.AssignmentsFragment;
import com.example.development.sakaiclientandroid.fragments.WebFragment;

import java.util.List;

/**
 * Created by Development on 6/23/18.
 */

public class AssignmentAdapter extends RecyclerView.Adapter {

    private List<AssignmentObject> assignments;
    private static boolean setFragmentManager;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleView;
        public TextView descriptionView;
        public TextView dueDateView;

        public ViewHolder(CardView cardView) {
            super(cardView);
            this.titleView = cardView.findViewById(R.id.assignment_name);
            this.descriptionView = cardView.findViewById(R.id.assignment_description);
            this.dueDateView = cardView.findViewById(R.id.assignment_date);
        }
    }

    public AssignmentAdapter(List<AssignmentObject> assignments) {
        this.assignments = assignments;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        if(!setFragmentManager) {
            NavActivity context = (NavActivity) parent.getContext();
            FragmentManager fragmentManager = context.getSupportFragmentManager();
            CustomLinkMovementMethod.setFragmentManager(fragmentManager);
            setFragmentManager = true;
        }

        // create a new view
        CardView v = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_cell_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ViewHolder viewHolder = (ViewHolder) holder;

        AssignmentObject assignment = assignments.get(position);
        viewHolder.titleView.setText(assignment.getTitle());

        // fromHtml(String) was deprecated in android N, so check the build version
        //before converting the html to text
        String instructions = assignment.getInstructions();
        instructions += "<br /> <a href='https://sakai.rutgers.edu/portal'>Take me here</a>";
        Spanned description;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description = Html.fromHtml(instructions, Html.FROM_HTML_MODE_LEGACY);
        } else {
            description = Html.fromHtml(instructions);
        };
        viewHolder.descriptionView.setText(description);
        viewHolder.descriptionView.setMovementMethod(CustomLinkMovementMethod.getInstance());

        StringBuilder dueDateText = new StringBuilder("Due: ");
        dueDateText.append(assignment.getDueTimeString());
        viewHolder.dueDateView.setText(dueDateText.toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return assignments.size();
    }


    private static class CustomLinkMovementMethod extends LinkMovementMethod {
        private static FragmentManager fragmentManager;
        private static CustomLinkMovementMethod mInstance;

        public static MovementMethod getInstance() {
            if (mInstance == null)
                mInstance = new CustomLinkMovementMethod();

            return mInstance;
        }

        public static void setFragmentManager(FragmentManager manager) {
            fragmentManager = manager;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            // Only open webview if something was clicked, instead of swiping left/right
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                // Get the location of the click
                int x = (int) event.getX();
                int y = (int) event.getY();

                // Adjust location for padding and scroll distance
                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                // Get the line at the calculated area
                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                // Get the link(s) clicked based on the selection location
                ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

                if (links.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        //links[0].onClick(widget);

                        if(links[0] instanceof URLSpan) {
                            URLSpan clicked = (URLSpan) links[0];
                            String link = clicked.getURL();
                            Log.d("Clicked", link);

                            if(fragmentManager != null) {
                                WebFragment fragment = WebFragment.newInstance(link);

                                fragmentManager.beginTransaction()
                                        .add(R.id.fragment_container, fragment)
                                        .commit();
                            }
                        }

                    } else { // action == MotionEvent.ACTION_DOWN
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(links[0]),
                                buffer.getSpanEnd(links[0]));
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                }
            }

            return super.onTouchEvent(widget, buffer, event);
        }
    }
}
