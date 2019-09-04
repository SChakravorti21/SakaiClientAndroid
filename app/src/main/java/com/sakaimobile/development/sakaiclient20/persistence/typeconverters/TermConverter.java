package com.sakaimobile.development.sakaiclient20.persistence.typeconverters;

import com.sakaimobile.development.sakaiclient20.models.Term;

import androidx.room.TypeConverter;

/**
 * Created by Development on 8/5/18.
 */

public class TermConverter {

    @TypeConverter
    public static Term fromTermEid(String termEid) {
        if(termEid == null || termEid.equals(""))
            return null;
        
        return new Term(termEid);
    }

    @TypeConverter
    public static String toTermEid(Term term) {
        if(term == null)
            return "";

        return term.getTermEid();
    }

}
