package ua.org.gostroy.stockwatcher.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import ua.org.gostroy.stockwatcher.client.StockPrice;
import ua.org.gostroy.stockwatcher.client.exception.DelistedException;

/**
 * Created by panser on 3/14/14.
 */
@RemoteServiceRelativePath("StockPriceService")
public interface StockPriceService extends RemoteService {
    StockPrice[] getPrices(String[] symbols) throws DelistedException;

/*
*
     * Utility/Convenience class.
     * Use StockPriceService.App.getInstance() to access static instance of StockPriceServiceAsync


    public static class App {
        private static final StockPriceServiceAsync ourInstance = (StockPriceServiceAsync) GWT.create(StockPriceService.class);

        public static StockPriceServiceAsync getInstance() {
            return ourInstance;
        }
    }
*/
}
