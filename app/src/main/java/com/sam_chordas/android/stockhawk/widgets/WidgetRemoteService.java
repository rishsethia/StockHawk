package com.sam_chordas.android.stockhawk.widgets;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

/**
 * Created by Rishabh on 8/30/16.
 */
public class WidgetRemoteService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new myFactory(getApplicationContext());
    }


    // my factory implementation
    public class myFactory implements RemoteViewsService.RemoteViewsFactory{

        Context mContext;
        Cursor mCursor;
        public myFactory(Context context){
             mContext = context;
        }


        @Override
        public void onCreate() {

            Thread thread = new Thread() {
                public void run() {
                    query();
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }


        public void query() {

            // too check that the stock is current is very important
            mCursor = mContext.getContentResolver().query(
                    QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{
                            QuoteColumns._ID,
                            QuoteColumns.SYMBOL,
                            QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE,
                            QuoteColumns.CHANGE,
                            QuoteColumns.ISUP},
                    QuoteColumns.ISCURRENT + " = ?",
                    new String[]{"1"},
                    null);
        }

        @Override
        public void onDataSetChanged() {

            // Getting the cursor from the content provider

            // on a separate thread
            Thread thread = new Thread() {
                public void run() {
                    query();
                }
            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
            }

        }


        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            mCursor.moveToFirst();
            RemoteViews mView = null;
            mCursor.moveToPosition(position);

            String symbolName = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL));
            String price = mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE));
            mView = new RemoteViews( mContext.getPackageName() , R.layout.list_item_quote);
            mView.setTextViewText(R.id.stock_symbol, symbolName );
            mView.setTextViewText(R.id.bid_price, price );
            mView.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));

            // Using the is_up field of the database to determine the color in the widget
            if (mCursor.getInt(mCursor.getColumnIndex("is_up")) == 1) {
                mView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                mView.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }



            Intent fillinIntent = new Intent();
            fillinIntent.putExtra("symbol",symbolName);
            fillinIntent.putExtra("price",price);
            mView.setOnClickFillInIntent(R.id.list_item,fillinIntent);
            return mView;
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
        public long getItemId(int position) {
            return position;
        }


        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
