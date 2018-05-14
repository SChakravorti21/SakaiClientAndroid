package com.example.development.sakaiclientandroid.models;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class Term implements Comparable<Term>{

    private static final Map<Integer, String> intToTerm = new HashMap<Integer, String>() {{

        put(0, "None");

        put(1, "Spring");
        put(6, "Summer");
        put(7, "Arresty");
        put(9, "Fall");
        put(12, "Winter");
    }};

    private int year;
    private int termInt;
    private String termString;

    public Term(String term_eid) {

        String[] splitTerm = term_eid.split(":");
        this.year = Integer.parseInt(splitTerm[0]);
        this.termInt = Integer.parseInt(splitTerm[1]);
        this.termString = intToTerm.get(this.termInt);
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

    public int getTerm() {
        return termInt;
    }

    public String getTermString() {
        return termString;
    }


}
