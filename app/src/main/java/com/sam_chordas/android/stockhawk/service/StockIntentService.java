package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  private Handler myHandler;

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    myHandler = new Handler();
    return super.onStartCommand(intent, flags, startId);
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra("tag").equals("add")){
      args.putString("symbol", intent.getStringExtra("symbol"));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    int q = stockTaskService.onRunTask(new TaskParams(intent.getStringExtra("tag"), args));


    if (q == -1){
      myHandler.post(new Runnable() {
        @Override
        public void run() {
          Toast.makeText(StockIntentService.this, "Incorrect stock symbol ", Toast.LENGTH_SHORT).show();
        }
      });

    }
  }
}
