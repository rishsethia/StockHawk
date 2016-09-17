package com.sam_chordas.android.stockhawk.ui;

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

    public class myFactory implements RemoteViewsService.RemoteViewsFactory{

        Context mContext;
        Cursor mCursor;
        public myFactory(Context context){
             mContext = context;
        }


        @Override
        public void onCreate() {

            mCursor = getApplicationContext().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
                    null, null);
        }

        @Override
        public void onDataSetChanged() {

            mCursor = getApplicationContext().getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                    new String[] { "Distinct " + QuoteColumns.SYMBOL }, null,
                    null, null);
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews mView = null;
           mCursor.moveToFirst();
            for (int i =0 ;  mCursor != null; i++){
                if(i == position){
                    mView = new RemoteViews( mContext.getPackageName() , R.layout.list_item_quote);
                    mView.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
                    mView.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
                    mView.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));
                }
                mCursor.moveToNext();
            }

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
