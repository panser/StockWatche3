package ua.org.gostroy.stockwatcher.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Created by panser on 3/14/14.
 */

class StockData extends JavaScriptObject {                              // (1)
    // Overlay types always have protected, zero argument constructors.
    protected StockData() {}                                              // (2)

    // JSNI methods to get stock data.
    public final native String getSymbol() /*-{ return this.symbol; }-*/; // (3)
    public final native double getPrice() /*-{ return this.price; }-*/;
    public final native double getChange() /*-{ return this.change; }-*/;

    // Non-JSNI method to return change percentage.                       // (4)
    public final double getChangePercent() {
        return 100.0 * getChange() / getPrice();
    }
}
