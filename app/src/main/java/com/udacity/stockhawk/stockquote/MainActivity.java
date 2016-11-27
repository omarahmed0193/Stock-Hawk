package com.udacity.stockhawk.stockquote;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.beans.Quote;
import com.udacity.stockhawk.graph.GraphActivity;
import com.udacity.stockhawk.service.StockIntentService;
import com.udacity.stockhawk.widget.StockWidgetProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    MaterialDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mProgressDialog = new MaterialDialog.Builder(this)
                .content(R.string.fetching_data)
                .progress(true, 0)
                .cancelable(false)
                .build();


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.symbol_search_dialog_title)
                        .content(R.string.symbol_search_dialog_message)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("FB", null, false, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                Realm realm = Realm.getDefaultInstance();
                                if (realm.where(Quote.class).equalTo("symbol", input.toString().toUpperCase()).findAll().size() != 0) {
                                    final Snackbar snackbar = Snackbar.make(view, input.toString().toUpperCase() + " Already exist", Snackbar.LENGTH_LONG);
                                    snackbar.setAction("Dismiss", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            snackbar.dismiss();
                                        }
                                    }).show();
                                } else {
                                    mProgressDialog.show();
                                    Intent stockServiceIntent = new Intent(MainActivity.this, StockIntentService.class);
                                    stockServiceIntent.putExtra("tag", "add");
                                    stockServiceIntent.putExtra("symbol", input.toString().toUpperCase());
                                    startService(stockServiceIntent);
                                }
                            }
                        }).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResposeCodeChanged(Integer code) {
        mProgressDialog.dismiss();
        String message = "Oops!";
        switch (code) {
            case 0:
                message = getString(R.string.wrong_symbol);
                break;
            case 400:
                message = getString(R.string.BAD_REQUEST);
                break;
            case 401:
                message = getString(R.string.UNAUTHORIZED);
                break;
            case 403:
                message = getString(R.string.FORBIDDEN);
                break;
            case 500:
                message = getString(R.string.INTERNAL_SERVER_ERROR);
                break;
            default:
        }
        final Snackbar snackbar = Snackbar.make(mToolbar, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        }).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataChanged(String status) {
        mProgressDialog.dismiss();
        if (status.equals("deleted")) {
            Intent intent = new Intent(this, StockWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), StockWidgetProvider.class)));
            sendBroadcast(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemClicked(Quote quote) {
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra(GraphActivity.EXTRA_QUOTE, quote);
        startActivity(intent);
    }
}
