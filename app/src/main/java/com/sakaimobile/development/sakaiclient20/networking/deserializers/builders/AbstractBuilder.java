package com.sakaimobile.development.sakaiclient20.networking.deserializers.builders;

/**
 * Created by Development on 8/5/18.
 */

public abstract class AbstractBuilder<TSource, TResult> {

    protected TSource source;
    protected TResult result;

    public AbstractBuilder(TSource source) {
        this.source = source;
    }

    public abstract AbstractBuilder<TSource, TResult> build() throws Exception;

    public TResult getResult() {
        return this.result;
    }
}
