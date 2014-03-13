package ua.org.gostroy.stockwatcher.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by panser on 3/14/14.
 */
public interface StockPriceServiceAsync {

    void getPrices(String[] symbols, AsyncCallback<StockPrice[]> async);
}
