package ua.org.gostroy.stockwatcher.client.exception;

import java.io.Serializable;

/**
 * Created by panser on 3/14/14.
 */
public class DelistedException extends Exception implements Serializable {
    private String symbol;

    public DelistedException(String symbol) {
        this.symbol = symbol;
    }

    public DelistedException() {
    }

    public String getSymbol() {
        return symbol;
    }
}
