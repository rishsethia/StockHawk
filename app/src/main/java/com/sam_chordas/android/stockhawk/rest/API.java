package com.sam_chordas.android.stockhawk.rest;

import com.sam_chordas.android.stockhawk.data.SymbolName;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Rishabh on 8/3/16.
 */
public interface API {
    @GET("/api/{name}")
    Call<List<SymbolName>> loadSymbols(@Path("name") String sym);
}
