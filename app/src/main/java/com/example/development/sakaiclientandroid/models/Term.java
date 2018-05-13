package com.example.development.sakaiclientandroid.models;

import java.util.HashMap;
import java.util.Map;

public class Term implements Comparable<Term>{

    private static final Map<Integer, String> intToTerm = new HashMap<Integer, String>() {{
        put(1, "Spring");
        put(9, "Fall");
        put(6, "Summer");
        put(12, "Winter");
    }};

    private int year;
    private int term;
    private String termString;

    public Term(int year, int term, String termString) {

        this.year = year;
        this.term = term;
        this.termString = termString;
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

        //if reach here, years are same
        if()

    }


    public int getYear() {
        return this.year;
    }

    public int getTerm() {
        return term;
    }

    public String getTermString() {
        return termString;
    }


}
