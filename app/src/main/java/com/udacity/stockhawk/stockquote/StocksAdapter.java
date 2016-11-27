package com.udacity.stockhawk.stockquote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.beans.Quote;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;


public class StocksAdapter extends RealmRecyclerViewAdapter<Quote, StocksAdapter.StockViewHolder> {

    private int mChangeUnits;

    public StocksAdapter(@NonNull Context context, @Nullable OrderedRealmCollection<Quote> data) {
        super(context, data, true);
        mChangeUnits = MainActivityFragment.PRICE;
    }

    @Override
    public StockViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_qoute, parent, false);
        return new StockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StockViewHolder holder, int position) {
        final Quote quote = getData().get(position);
        holder.mStockSymbol.setText(quote.getSymbol());
        holder.mStockSymbol.setContentDescription(quote.getSymbol());
        holder.mBidPrice.setText(quote.getBid());
        holder.mBidPrice.setContentDescription(context.getString(R.string.bid_price_is) + quote.getBid());
        holder.mChange.setBackgroundResource(Double.valueOf(quote.getChange()) > 0 ? R.drawable.percent_change_pill_green : R.drawable.percent_change_pill_red);
        holder.mChange.setText(mChangeUnits == MainActivityFragment.PRICE ? quote.getChange() : quote.getChangeinPercent());
        holder.mChange.setContentDescription(mChangeUnits == MainActivityFragment.PRICE ? context.getString(R.string.the_change_is) + quote.getChange() : context.getString(R.string.the_percentage_change_is) + quote.getChangeinPercent());
        holder.mParentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(quote);
            }
        });
    }

    public class StockViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.stock_symbol)
        TextView mStockSymbol;
        @BindView(R.id.bid_price)
        TextView mBidPrice;
        @BindView(R.id.change)
        TextView mChange;
        View mParentView;

        public StockViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mParentView = itemView;
        }
    }

    public void setChangeUnits(int unit) {
        mChangeUnits = unit;
    }

    public int getChangeUnits() {
        return mChangeUnits;
    }
}
