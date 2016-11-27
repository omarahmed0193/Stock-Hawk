package com.udacity.stockhawk.beans;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StockGraphQuery {

    @SerializedName("query")
    @Expose
    private Query query;

    public List<Quote> getQuotes() {
        return query.getResults().getQuotes();
    }

    private class Query {

        @SerializedName("results")
        @Expose
        private Quotes results;

        public Quotes getResults() {
            return results;
        }
    }

    private class Quotes {

        @SerializedName("quote")
        @Expose
        private List<Quote> quotes;

        public List<Quote> getQuotes() {
            return quotes;
        }
    }

    public class Quote {
        @SerializedName("Symbol")
        @Expose
        private String symbol;
        @SerializedName("Date")
        @Expose
        private String date;
        @SerializedName("Open")
        @Expose
        private String open;
        @SerializedName("High")
        @Expose
        private String high;
        @SerializedName("Low")
        @Expose
        private String low;
        @SerializedName("Close")
        @Expose
        private String close;
        @SerializedName("Volume")
        @Expose
        private String volume;
        @SerializedName("Adj_Close")
        @Expose
        private String adjClose;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getOpen() {
            return open;
        }

        public void setOpen(String open) {
            this.open = open;
        }

        public String getHigh() {
            return high;
        }

        public void setHigh(String high) {
            this.high = high;
        }

        public String getLow() {
            return low;
        }

        public void setLow(String low) {
            this.low = low;
        }

        public String getClose() {
            return close;
        }

        public void setClose(String close) {
            this.close = close;
        }

        public String getVolume() {
            return volume;
        }

        public void setVolume(String volume) {
            this.volume = volume;
        }

        public String getAdjClose() {
            return adjClose;
        }

        public void setAdjClose(String adjClose) {
            this.adjClose = adjClose;
        }
    }
}
