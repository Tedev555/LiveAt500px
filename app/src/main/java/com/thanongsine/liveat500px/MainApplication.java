package com.thanongsine.liveat500px;

import android.app.Application;

import com.inthecheesefactory.thecheeselibrary.manager.Contextor;

/**
 * Created by Ted555 on 7/16/2016.
 */
public class MainApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        //initialize thing(s) here
        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
