package com.udacity.stockhawk.beans;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StocksQuery {

    @SerializedName("query")
    @Expose
    private Query query;

    public List<Quote> getStocksQuotes() {
        return query.getResult().getQuotes();
    }

    private class Query {

        @SerializedName("results")
        private Result result;

        Result getResult() {
            return result;
        }
    }

    private class Result {

        @SerializedName("quote")
        private List<Quote> quote;

        List<Quote> getQuotes() {
            return quote;
        }
    }

}
