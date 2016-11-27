
package com.udacity.stockhawk.beans;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StockQuery {

    @SerializedName("query")
    @Expose
    private Query query;

    public Quote getStockQuote() {
        return query.getResult().getQuote();
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
        private Quote quote;

        Quote getQuote() {
            return quote;
        }
    }

}
