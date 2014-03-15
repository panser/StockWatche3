package ua.org.gostroy.stockwatcher.client;

/**
 * Created by panser on 3/15/14.
 */

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class StockWatcherTest extends GWTTestCase {                       // (1)
    /**
     * Must refer to a valid module that sources this class.
     */
    public String getModuleName() {                                         // (2)
        return "ua.org.gostroy.stockwatcher.StockWatcher";
    }

    /**
     * Add as many tests as you like.
     */
    public void testSimple() {                                              // (3)
        assertTrue(true);
    }

    /**  * Verify that the instance fields in the StockPrice class are set correctly.  */
    public void testStockPriceCtor() {
        String symbol = "XYZ";
        double price = 70.0;
        double change = 2.0;
        double changePercent = 100.0 * change / price;
        StockPrice sp = new StockPrice(symbol, price, change);

        assertNotNull(sp);
        assertEquals(symbol, sp.getSymbol());
        assertEquals(price, sp.getPrice(), 0.001);
        assertEquals(change, sp.getChange(), 0.001);
        assertEquals(changePercent, sp.getChangePercent(), 0.001); }
}