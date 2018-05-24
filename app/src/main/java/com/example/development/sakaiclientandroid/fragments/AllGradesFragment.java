package com.example.development.sakaiclientandroid.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.development.sakaiclientandroid.R;
import com.example.development.sakaiclientandroid.models.Course;
import com.example.development.sakaiclientandroid.models.Term;
import com.example.development.sakaiclientandroid.utils.DataHandler;
import com.example.development.sakaiclientandroid.utils.GradebookTermsExpListAdapter;
import com.example.development.sakaiclientandroid.utils.RequestCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllGradesFragment extends BaseFragment {

    private List<String> termHeaders;
    private HashMap<String, List<String>> termToCourseTitles;
    private HashMap<String, List<String>> termToCourseIds;
    private HashMap<String, List<Integer>> termToCourseSubjectCodes;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_grades, null);

        //we can prepare the headers before the request is successful because the grades
        //don't need to be in there for us to sort it.
        prepareHeadersAndChildren(DataHandler.getCoursesSortedByTerm());

        final ExpandableListView expandableListView = view.findViewById(R.id.all_grades_listview);


        //if we already have grades for all sites cached, then no need to make another request
        if(DataHandler.gradesRequestedForAllSites()) {

            GradebookTermsExpListAdapter adapter = new GradebookTermsExpListAdapter(mContext, termHeaders, termToCourseTitles, termToCourseIds, termToCourseSubjectCodes);
            expandableListView.setAdapter(adapter);
        }
        //if we have not, then must make request
        else {
            DataHandler.requestAllGrades(new RequestCallback() {

                @Override
                public void onAllGradesSuccess() {

                    GradebookTermsExpListAdapter adapter = new GradebookTermsExpListAdapter(mContext, termHeaders, termToCourseTitles, termToCourseIds, termToCourseSubjectCodes);
                    expandableListView.setAdapter(adapter);
                }


                @Override
                public void onAllGradesFailure(Throwable throwable) {

                    //TODO failure message

                }

            });
        }

        return view;
    }




    /**
     * takes an ArrayList of SiteCollections already organized and sorted by term
     * puts that data into the headersList and child HashMap to be displayed in the
     * expandable list view in the current activity
     *
     * @param sorted ArrayList of ArrayList of Course Objects
     */
    private void prepareHeadersAndChildren(ArrayList<ArrayList<Course>> sorted) {


        this.termHeaders = new ArrayList<>();
        this.termToCourseTitles = new HashMap<>();
        this.termToCourseSubjectCodes = new HashMap<>();
        this.termToCourseIds = new HashMap<>();

        //sets the Term as the headers for the expandable list view
        //each child is the name of the site in that term
        for(ArrayList<Course> coursesPerTerm : sorted) {

            //we can just look at the first site's term, since all the terms
            //should be the same, since we already sorted
            Term currTerm = coursesPerTerm.get(0).getTerm();

            String termKey = currTerm.getTermString();

            //don't put the year if the header is just General
            if(!termKey.equals("General")) {
                termKey += (" " + currTerm.getYear());
            }

            this.termHeaders.add(termKey);


            List<String> tempChildList = new ArrayList<>();
            List<Integer> tempSubjectCodeList = new ArrayList<>();
            List<String> tempSiteIdList = new ArrayList<>();

            //places the title of each site and its corresponding ImgResId into 2 lists
            //which are then added to the hashmap under the current term header
            for(Course currCourse : coursesPerTerm) {

                tempChildList.add(currCourse.getTitle());
                tempSiteIdList.add(currCourse.getId());

                //TODO figure out a way to add the resource Id values directly, for more abstraction
                //adds subject code to hashmap
                int subjectCode = currCourse.getSubjectCode();
                tempSubjectCodeList.add(subjectCode);
//                int resId = RutgersSubjectCodes.getResourceIdFromSubjectCode(subjectCode, getActivity().getPackageName(), getContext());
//                tempSubjectCodeList.add(resId);
            }

            this.termToCourseIds.put(termKey, tempSiteIdList);
            this.termToCourseSubjectCodes.put(termKey, tempSubjectCodeList);
            this.termToCourseTitles.put(termKey, tempChildList);
        }


    }
}
