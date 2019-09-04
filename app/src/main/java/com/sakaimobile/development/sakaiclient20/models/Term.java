package com.sakaimobile.development.sakaiclient20.models;

import java.io.Serializable;

import androidx.annotation.NonNull;

public class Term implements Comparable<Term>, Serializable{

    private int year;
    private int termInt;
    private String termEid;
    private String termString;

    public Term(String termEid) {
        this.termEid = termEid;

        String[] splitTerm = termEid.split(":");
        this.year = Integer.parseInt(splitTerm[0]);
        this.termInt = Integer.parseInt(splitTerm[1]);

        this.termString = termIntToSemester(this.termInt);
    }

    private static String termIntToSemester(int termInt) {
        if (termInt == 0) {
            return "General";
        } else if (termInt >= 12) {
            return "Winter";
        } else if (termInt >= 9) {
            return "Fall";
        } else if (termInt >= 6) {
            return "Summer";
        } else {
            return "Spring";
        }
    }

    @Override
    public int compareTo(@NonNull Term other) {
        // First compare the years
        if (this.year < other.year) {
            return -1;
        }
        if (this.year > other.year) {
            return 1;
        }

        // If the year is the same, compare the semesters
        if (this.termInt < other.termInt) {
            return -1;
        } else if (this.termInt > other.termInt) {
            return 1;
        }

        // Otherwise the semesters are also the same, terms are equal
        return 0;

    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Term && this.compareTo((Term) obj) == 0;
    }

    @Override
    public String toString() {
        // "General" case
        if(this.termInt == 0)
            return this.termString;

        // Otherwise combine the semester and year, eg. "Fall 2018"
        return this.termString + "  " + this.year;
    }

    public int getYear() {
        return this.year;
    }

    public int getTermInt() {
        return this.termInt;
    }

    public String getTermEid() {
        return this.termEid;
    }

    public String getTermString() {
        return this.termString;
    }

}
