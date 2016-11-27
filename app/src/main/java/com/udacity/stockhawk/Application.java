package com.udacity.stockhawk;

import io.realm.Realm;

/**
 * Created by Omar on 11/11/2016.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
