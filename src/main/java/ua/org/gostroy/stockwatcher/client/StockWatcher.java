package ua.org.gostroy.stockwatcher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import ua.org.gostroy.stockwatcher.client.exception.DelistedException;
import ua.org.gostroy.stockwatcher.client.service.StockPriceService;
import ua.org.gostroy.stockwatcher.client.service.StockPriceServiceAsync;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by panser on 3/13/14.
 */
public class StockWatcher implements EntryPoint {
    private static final int REFRESH_INTERVAL = 5000; // ms
    private VerticalPanel mainPanel = new VerticalPanel();
    private FlexTable stocksFlexTable = new FlexTable();
    private HorizontalPanel addPanel = new HorizontalPanel();
    private TextBox newSymbolTextBox = new TextBox();
    private Button addStockButton = new Button("Add");
    private Label lastUpdatedLabel = new Label();
    private Label errorMsgLabel = new Label();
    private ArrayList<String> stocks = new ArrayList<String>();
    private StockPriceServiceAsync stockPriceSvc;
//    private StockPriceServiceAsync stockPriceSvc = GWT.create(StockPriceService.class);
    private static final String JSON_URL = GWT.getModuleBaseURL() + "stockPrices?q=";

    public void onModuleLoad() {
        // Create table for stock data.
        stocksFlexTable.setText(0, 0, "Symbol");
        stocksFlexTable.setText(0, 1, "Price");
        stocksFlexTable.setText(0, 2, "Change");
        stocksFlexTable.setText(0, 3, "Remove");
        stocksFlexTable.setCellPadding(6);

        // Add styles to elements in the stock list table.
        stocksFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
        stocksFlexTable.addStyleName("watchList");
        stocksFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(0, 3, "watchListRemoveColumn");

        // Assemble Add Stock panel.
        addPanel.add(newSymbolTextBox);
        addPanel.add(addStockButton);
        addPanel.addStyleName("addPanel");

        // Assemble Main panel.
        errorMsgLabel.setStyleName("errorMessage");
        errorMsgLabel.setVisible(false);

        // Assemble Main panel.
        mainPanel.add(errorMsgLabel);
        mainPanel.add(stocksFlexTable);
        mainPanel.add(addPanel);
        mainPanel.add(lastUpdatedLabel);

        // Associate the Main panel with the HTML host page.
        RootPanel.get("stockList").add(mainPanel);

        // Move cursor focus to the input box.
        newSymbolTextBox.setFocus(true);

        // Setup timer to refresh list automatically.
        Timer refreshTimer = new Timer(){
            @Override
            public void run() {
                refreshWatchList();
            }
        };
        refreshTimer.scheduleRepeating(REFRESH_INTERVAL);


        // Listen for mouse events on the Add button.
        addStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addStock();
            }
        });

        // Listen for keyboard events in the input box.
        newSymbolTextBox.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
                    addStock();
                }
            }
        });
    }

    /**
     * Add stock to FlexTable. Executed when the user clicks the addStockButton or
     * presses enter in the newSymbolTextBox.
     */
    private void addStock() {
        final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
        newSymbolTextBox.setFocus(true);

        if(!symbol.matches("^[A-Z0-9\\.]{1,10}$")){
            Window.alert("'" + symbol + "' is not a valid symbol.");
            newSymbolTextBox.selectAll();
            return;
        }

        newSymbolTextBox.setText("");

        if(stocks.contains(symbol)){
            return;
        }

        int row = stocksFlexTable.getRowCount();
        stocks.add(symbol);
        stocksFlexTable.setText(row,0,symbol);
        stocksFlexTable.setWidget(row, 2, new Label());
        stocksFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
        stocksFlexTable.getCellFormatter().addStyleName(row, 3, "watchListRemoveColumn");

        // Add a button to remove this stock from the table.
        Button removeStockButton = new Button("x");
        removeStockButton.addStyleDependentName("remove");
        removeStockButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                int removeIndex = stocks.indexOf(symbol);
                stocks.remove(removeIndex);
                stocksFlexTable.removeRow(removeIndex  +1);
            }
        });
        stocksFlexTable.setWidget(row,3,removeStockButton);

        refreshWatchList();
    }

    private void refreshWatchList() {
/*
    // first attempt: user local generated data

        final double MAX_PRICE = 100.0; // $100.00
        final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

        StockPrice[] prices = new StockPrice[stocks.size()];
        for (int i = 0; i < stocks.size(); i++) {
            double price = Random.nextDouble() * MAX_PRICE;
            double change = price * MAX_PRICE_CHANGE
                    * (Random.nextDouble() * 2.0 - 1.0);

            prices[i] = new StockPrice(stocks.get(i), price, change);
        }

        updateTable(prices);
*/

/*
    // second attempt : use data generated on server and receive over RPC service

        stockPriceSvc = StockPriceService.App.getInstance();

        // Set up the callback object.
        AsyncCallback<StockPrice[]> callback = new AsyncCallback<StockPrice[]>() {
            public void onFailure(Throwable caught) {

            // If the stock code is in the list of delisted codes, display an error message.
            String details = caught.getMessage();
            if (caught instanceof DelistedException) {
                details = "Company '" + ((DelistedException)caught).getSymbol() + "' was delisted";
            }

            errorMsgLabel.setText("Error: " + details);
            errorMsgLabel.setVisible(true);
            }

            public void onSuccess(StockPrice[] result) {
                updateTable(result);
            }
        };

        // Make the call to the stock price service.
        stockPriceSvc.getPrices(stocks.toArray(new String[0]), callback);
*/

    // third attempt : use data generated on server and receive over JSON

        if (stocks.size() == 0) {  return;  }

        String url = JSON_URL;
        // Append watch list stock symbols to query URL.
        Iterator<String> iter = stocks.iterator();
        while (iter.hasNext()) {
            url += iter.next();
            if (iter.hasNext()) {
                url += "+";
            }
        }
        url = URL.encode(url);

        // Send request to server and catch any errors.
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

        try {
            Request request = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    displayError("Couldn't retrieve JSON");
                }

                public void onResponseReceived(Request request, Response response) {
                    if (200 == response.getStatusCode()) {
                        updateTable(JsonUtils.<JsArray<StockData>>safeEval(response.getText()));
                    } else {
                        displayError("Couldn't retrieve JSON (" + response.getStatusText() + ")");
                    }
                }
            });
        } catch (RequestException e) {
            displayError("Couldn't retrieve JSON");
        }

    }

/*
    //    second attempt : use data generated on server and receive over RPC service
    private void updateTable(StockPrice[] prices) {
        for (int i = 0; i < prices.length; i++) {
            updateTable(prices[i]);
        }
        // Display timestamp showing last refresh.
        lastUpdatedLabel.setText("Last update : "  + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
        // Clear any errors.
        errorMsgLabel.setVisible(false);
    }
*/

    private void updateTable(JsArray<StockData> prices) {
        for (int i = 0; i < prices.length(); i++) {
            updateTable(prices.get(i));
        }

        // Display timestamp showing last refresh.
        lastUpdatedLabel.setText("Last update : " + DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
        // Clear any errors.
        errorMsgLabel.setVisible(false);
    }

//    private void updateTable(StockPrice price) {
    private void updateTable(StockData price) {
        // Make sure the stock is still in the stock table.
        if (!stocks.contains(price.getSymbol())) {
            return;
        }

        int row = stocks.indexOf(price.getSymbol()) + 1;

        // Format the data in the Price and Change fields.
        String priceText = NumberFormat.getFormat("#,##0.00").format(price.getPrice());
        NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
        String changeText = changeFormat.format(price.getChange());
        String changePercentText = changeFormat.format(price.getChangePercent());

        // Populate the Price and Change fields with new data.
        stocksFlexTable.setText(row, 1, priceText);
//        stocksFlexTable.setText(row, 2, changeText + " (" + changePercentText + "%)");
        Label changeWidget = (Label)stocksFlexTable.getWidget(row, 2);
        changeWidget.setText(changeText + " (" + changePercentText + "%)");

        // Change the color of text in the Change field based on its value.
        String changeStyleName = "noChange";
        if (price.getChangePercent() < -0.1f) {
            changeStyleName = "negativeChange";
        }
        else if (price.getChangePercent() > 0.1f) {
            changeStyleName = "positiveChange";
        }
        changeWidget.setStyleName(changeStyleName);
    }

    /**  * If can't get JSON, display error message.  * @param error  */
    private void displayError(String error) {
        errorMsgLabel.setText("Error: " + error);
        errorMsgLabel.setVisible(true);
    }
}
