package ua.org.gostroy.stockwatcher.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;

/**
 * Created by panser on 3/14/14.
 */
@RemoteServiceRelativePath("StockPriceService")
public interface StockPriceService extends RemoteService {
    StockPrice[] getPrices(String[] symbols);

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
