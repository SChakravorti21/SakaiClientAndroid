package com.example.development.sakaiclientandroid.utils.custom;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.api_models.gradebook.GradebookObject;

import java.util.List;

public class GradeItemAdapter extends ArrayAdapter {

    private static final float PADDING_LEFT_DP = 10f;
    private static final float PADDING_VERTICAL_DP = 8f;


    private final Context context;
    private final List<GradebookObject> assignmentsList;

    public GradeItemAdapter(Context context, List<GradebookObject> assignmentsList) {
        super(context, R.layout.gradeitem_node, assignmentsList);

        this.context = context;
        this.assignmentsList = assignmentsList;
    }


    /**
     * Convert dp to pixel for the phone
     * @param dp number of dp
     * @return the dp in pixels
     */
    private int convertDpToPx(float dp)
    {
        int paddingPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        );

        return paddingPx;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        if(convertView == null) {
            //create inflater
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            //inflate the row
            convertView = inflater.inflate(R.layout.gradeitem_node, parent, false);

            //get name and grade textviews
            TextView nameTextView = convertView.findViewById(R.id.txt_assignment_name);
            TextView gradeTextView = convertView.findViewById(R.id.txt_grade);

            //set the text
            nameTextView.setText(assignmentsList.get(position).getItemName());
            gradeTextView.setText(assignmentsList.get(position).getGrade() + "/" + assignmentsList.get(position).getPoints());


            //set padding on the name view
            int paddingLeftPx = convertDpToPx(PADDING_LEFT_DP);
            int paddingVerticalPx = convertDpToPx(PADDING_VERTICAL_DP);
            nameTextView.setPadding(paddingLeftPx, paddingVerticalPx, 0, paddingVerticalPx);


            Animation anim = AnimationUtils.loadAnimation(context, R.anim.listview_anim);
            anim.setStartOffset(position * 100);
            convertView.startAnimation(anim);

        }

        return convertView;

    }
}
