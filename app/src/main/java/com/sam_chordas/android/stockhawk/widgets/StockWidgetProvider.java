package com.sam_chordas.android.stockhawk.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.DetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {

            Intent myIntent = new Intent(context, WidgetRemoteService.class);
            myIntent.setData(Uri.parse(myIntent.toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_view);
            rv.setRemoteAdapter(R.id.rec_view , myIntent);


            // Setting up the pending intent template
            Intent intent  = new Intent(context,DetailActivity.class);
            PendingIntent myPendingIntent = PendingIntent.getActivity(context,0,intent,0);
            rv.setPendingIntentTemplate(R.id.rec_view,myPendingIntent);

            //TODO Add for the empty view here


            appWidgetManager.updateAppWidget(appWidgetId, rv);


        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }


}

