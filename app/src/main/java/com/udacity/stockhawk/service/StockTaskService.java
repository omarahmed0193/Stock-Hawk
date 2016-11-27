package com.udacity.stockhawk.service;


import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.udacity.stockhawk.beans.Quote;
import com.udacity.stockhawk.beans.StockQuery;
import com.udacity.stockhawk.beans.StocksQuery;
import com.udacity.stockhawk.retrofit.ServiceGenerator;
import com.udacity.stockhawk.retrofit.StockApiService;
import com.udacity.stockhawk.widget.StockWidgetProvider;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StockTaskService extends GcmTaskService {

    public static final String EXTRA_SYMBOL = "extra_symbol";
    private StockApiService mStockApiService;
    private int mResult;
    private Context mContext;

    public StockTaskService(Context context) {
        mContext = context;
    }

    public StockTaskService() {
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        if (mContext == null) {
            mContext = this;
        }
        String tag = taskParams.getTag();
        mResult = GcmNetworkManager.RESULT_SUCCESS;
        mStockApiService = ServiceGenerator.createService(StockApiService.class);
        String query = "select * from yahoo.finance.quotes where symbol in (";
        if (tag.equals("periodic") || tag.equals("init")) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Quote> quotes = realm.where(Quote.class).findAll();
            if (quotes.load()) {
                if (quotes.size() == 0) {
                    getStocksQuotes(query + "\"YHOO\",\"AAPL\",\"GOOG\",\"MSFT\")");
                } else if (quotes.size() == 1) {
                    getStockQuote(query + "\"" + quotes.get(0).getSymbol() + "\")");
                } else {
                    String symbols = "";
                    for (int i = 0; i < quotes.size(); i++) {
                        symbols += "\"" + quotes.get(i).getSymbol() + (i == quotes.size() - 1 ? "\")" : "\",");
                    }
                    getStocksQuotes(query + symbols);
                }
                realm.close();
            }
        } else if (tag.equals("add")) {
            getStockQuote(query + "\"" + taskParams.getExtras().getString(EXTRA_SYMBOL) + "\")");
        }
        return mResult;
    }

    private void getStockQuote(final String query) {
        mStockApiService.getStock(query).enqueue(new Callback<StockQuery>() {
            @Override
            public void onResponse(Call<StockQuery> call, Response<StockQuery> response) {
                if (response.isSuccessful()) {
                    Quote quote = response.body().getStockQuote();
                    if (quote.getBid() != null && quote.getName() != null && quote.getChange() != null && quote.getChangeinPercent() != null) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(quote);
                        realm.commitTransaction();
                        realm.close();
                        EventBus.getDefault().post("added_single");
                        updateWidget();
                    } else {
                        EventBus.getDefault().post(0);
                    }
                } else {
                    EventBus.getDefault().post(response.code());
                }
            }

            @Override
            public void onFailure(Call<StockQuery> call, Throwable t) {
                mResult = GcmNetworkManager.RESULT_FAILURE;
            }
        });
    }

    private void getStocksQuotes(String query) {
        mStockApiService.getStocks(query).enqueue(new Callback<StocksQuery>() {
            @Override
            public void onResponse(Call<StocksQuery> call, Response<StocksQuery> response) {
                if (response.isSuccessful()) {
                    Realm realm = Realm.getDefaultInstance();
                    List<Quote> quotes = response.body().getStocksQuotes();
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(quotes);
                    realm.commitTransaction();
                    realm.close();
                    EventBus.getDefault().post("added_multiple");
                    updateWidget();
                } else {
                    EventBus.getDefault().post(response.code());
                }
            }

            @Override
            public void onFailure(Call<StocksQuery> call, Throwable t) {
                mResult = GcmNetworkManager.RESULT_FAILURE;
            }
        });
    }

    private void updateWidget() {
        Intent intent = new Intent(mContext, StockWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(mContext).getAppWidgetIds(new ComponentName(mContext, StockWidgetProvider.class)));
        mContext.sendBroadcast(intent);
    }
}
