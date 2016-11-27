package com.udacity.stockhawk.retrofit;

import com.udacity.stockhawk.beans.StockGraphQuery;
import com.udacity.stockhawk.beans.StockQuery;
import com.udacity.stockhawk.beans.StocksQuery;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Omar on 11/10/2016.
 */

public interface StockApiService {
    @GET("/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<StockQuery> getStock(@Query("q") String query);
    @GET("/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<StocksQuery> getStocks(@Query("q") String query);
    @GET("/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=")
    Call<StockGraphQuery> getStocksWithDateSpan(@Query("q") String query);
}
