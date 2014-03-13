package ua.org.gostroy.stockwatcher.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ua.org.gostroy.stockwatcher.client.StockPrice;

/**
 * Created by panser on 3/14/14.
 */
public interface StockPriceServiceAsync {

    void getPrices(String[] symbols, AsyncCallback<StockPrice[]> async);
}
