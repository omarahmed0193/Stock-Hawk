package com.udacity.stockhawk.widget;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.beans.Quote;
import com.udacity.stockhawk.graph.GraphActivity;

import java.util.List;

import io.realm.Realm;


public class StockWidgetService extends RemoteViewsService {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new StockRemoteViewsFactory(this.getApplicationContext());
    }

    private class StockRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private List<Quote> mQuotes;
        private Realm mRealm;

        public StockRemoteViewsFactory(Context context) {
            mContext = context;
        }

        @Override
        public void onCreate() {
            mRealm = Realm.getDefaultInstance();
            mQuotes = mRealm.copyFromRealm(mRealm.where(Quote.class).findAll());
            mRealm.close();
        }

        @Override
        public void onDataSetChanged() {
            mRealm = Realm.getDefaultInstance();
            mQuotes = mRealm.copyFromRealm(mRealm.where(Quote.class).findAll());
            mRealm.close();
        }

        @Override
        public void onDestroy() {
        }

        @Override
        public int getCount() {
            return mQuotes.size();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.list_item_widget_quote);
            rv.setTextViewText(R.id.stock_symbol, mQuotes.get(i).getSymbol());
            rv.setTextViewText(R.id.change, mQuotes.get(i).getChange());
            rv.setTextViewText(R.id.bid_price, mQuotes.get(i).getBid());

            Intent fillIntent = new Intent();
            fillIntent.putExtra(GraphActivity.EXTRA_QUOTE, mQuotes.get(i));
            rv.setOnClickFillInIntent(R.id.stock_widget_item, fillIntent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
