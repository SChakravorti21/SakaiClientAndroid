package com.example.development.sakaiclientandroid.models;

import android.content.res.Resources;
import android.util.Log;

import com.example.development.sakaiclientandroid.R;

import java.util.HashMap;
import java.util.Map;

import static java.security.AccessController.getContext;

public class Term implements Comparable<Term>{

    private int year;
    private int termInt;
    private String termString;

    public Term(String term_eid) {

        String[] splitTerm = term_eid.split(":");
        this.year = Integer.parseInt(splitTerm[0]);
        this.termInt = Integer.parseInt(splitTerm[1]);

        this.termString = intToTerm(this.termInt);
    }


    private static String intToTerm(int termInt) {

        if(termInt == 0) {
            return Resources.getSystem().getString(R.string.none);
        }
        else if(termInt >= 12) {
            return Resources.getSystem().getString(R.string.winter);
        }
        else if(termInt >= 9) {
            return Resources.getSystem().getString(R.string.fall);
        }
        else if(termInt >= 6) {
            return Resources.getSystem().getString(R.string.summer);
        }
        else {
            return Resources.getSystem().getString(R.string.spring);
        }
    }


    @Override
    public int compareTo(Term other) {

        if(other == null) {
            return 1;
        }

        if(this.year < other.year) {
            return -1;
        }
        else if(this.year > other.year) {
            return 1;
        }

        if(this.termInt < other.termInt) {
            return -1;
        }
        else if(this.termInt > other.termInt) {
            return 1;
        }

        return 0;
    }


    public int getYear() {
        return this.year;
    }

    public int getTermInt() {
        return termInt;
    }

    public String getTermString() {
        return termString;
    }


}
