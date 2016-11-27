package com.udacity.stockhawk.stockquote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utils;
import com.udacity.stockhawk.beans.Quote;
import com.udacity.stockhawk.service.StockIntentService;
import com.udacity.stockhawk.service.StockTaskService;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    @BindView(R.id.stock_recycler_view)
    RecyclerView mStockRecyclerView;
    @BindView(R.id.empty_state_text_view)
    TextView mEmptyStateTextView;

    public static final int PRICE = 0;
    public static final int PERCENTAGE = 1;
    private Realm mRealm;
    private StocksAdapter mStocksAdapter;
    private RealmResults<Quote> mQuotes;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        if (savedInstanceState == null) {
            initServices();
        }

        mRealm = Realm.getDefaultInstance();

        initStockAdapter();

        initSwipeToDelete();


        return view;
    }

    private void initStockAdapter() {
        mQuotes = mRealm.where(Quote.class).findAll();
        if (mQuotes.size() == 0) {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            if (!Utils.isConnected(getActivity())) {
                mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
            } else {
                mEmptyStateTextView.setText(getString(R.string.no_data_available));
            }
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
        mStocksAdapter = new StocksAdapter(getActivity(),
                mQuotes);
        mStockRecyclerView.setAdapter(mStocksAdapter);

        mStocksAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                mEmptyStateTextView.setVisibility(mStocksAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        });
    }

    private void initSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mRealm.beginTransaction();
                mStocksAdapter.getItem(viewHolder.getAdapterPosition()).deleteFromRealm();
                mRealm.commitTransaction();
                EventBus.getDefault().post("deleted");
            }
        });
        itemTouchHelper.attachToRecyclerView(mStockRecyclerView);
    }


    private void initServices() {
        Intent stockServiceIntent = new Intent(getActivity(), StockIntentService.class);
        stockServiceIntent.putExtra("tag", "init");
        getActivity().startService(stockServiceIntent);

        long period = 3600000L;
        long flex = 10L;
        String periodicTag = "periodic";

        // create a periodic task to pull stocks once every hour after the app has been opened. This
        // is so Widget data stays up to date.
        PeriodicTask periodicTask = new PeriodicTask.Builder()
                .setService(StockTaskService.class)
                .setPeriod(period)
                .setFlex(flex)
                .setTag(periodicTag)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();
        // Schedule task with tag "periodic." This ensure that only the stocks present in the DB
        // are updated.
        GcmNetworkManager.getInstance(getActivity()).schedule(periodicTask);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_change_units) {
            if (mStocksAdapter.getChangeUnits() == PRICE) {
                mStocksAdapter.setChangeUnits(PERCENTAGE);
            } else if (mStocksAdapter.getChangeUnits() == PERCENTAGE) {
                mStocksAdapter.setChangeUnits(PRICE);
            }
            mStocksAdapter.notifyDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
