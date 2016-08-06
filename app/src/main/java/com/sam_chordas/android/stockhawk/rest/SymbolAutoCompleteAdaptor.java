package com.sam_chordas.android.stockhawk.rest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.SymbolName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rishabh on 8/3/16.
 */
public class SymbolAutoCompleteAdaptor extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 10;
    private Context mContext;

    private List<SymbolName> resultList = new ArrayList<SymbolName>();
    List<SymbolName> mySymbols  = new ArrayList<>();

    public SymbolAutoCompleteAdaptor(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Object getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.simple_dropdown, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.symbolTextView)).setText(resultList.get(position).getSymbol());
        ((TextView) convertView.findViewById(R.id.nameTextView)).setText(resultList.get(position).getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null){
                    List<SymbolName> mySymbols = findSymbols(mContext, constraint.toString());
                    filterResults.values = mySymbols;
                    filterResults.count = mySymbols.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    resultList = (List<SymbolName>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }

            }
        };

        return filter;
    }

    private List<SymbolName> findSymbols(Context mContext, String s) {

        String url = "http://chstocksearch.herokuapp.com/api/"
                + s ;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://chstocksearch.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API myAPI = retrofit.create(API.class);


        Call<List<SymbolName>> call = myAPI.loadSymbols(s);

        try {
            mySymbols = call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mySymbols;
    }

}
