package com.udacity.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.TaskParams;


public class StockIntentService extends IntentService {

    public StockIntentService(){
        super(StockIntentService.class.getName());
    }

    public StockIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        StockTaskService stockTaskService = new StockTaskService(this);
        Bundle args = new Bundle();
        if (intent.getStringExtra("tag").equals("add")){
            args.putString(StockTaskService.EXTRA_SYMBOL, intent.getStringExtra("symbol"));
        }
        stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
