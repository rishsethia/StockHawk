package com.sam_chordas.android.stockhawk.data;

/**
 * Created by Rishabh on 8/3/16.
 */
public class SymbolName {
    String company;
    String symbol;
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return company;
    }

    public void setName(String name) {
        this.company = name;
    }

    public String toString(){
        return this.symbol;
    }
}
