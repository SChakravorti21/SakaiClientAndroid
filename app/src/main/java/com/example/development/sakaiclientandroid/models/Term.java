package com.example.development.sakaiclientandroid.models;


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
            return "General";
        }

        else if(termInt >= 12) {
            return "Winter";
        }
        else if(termInt >= 9) {
            return "Fall";
        }
        else if(termInt >= 6) {
            return "Summer";
        }
        else {
            return "Spring";
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

    @Override
    public String toString() {
        return this.termString + "  " + this.termInt;
    }


}
