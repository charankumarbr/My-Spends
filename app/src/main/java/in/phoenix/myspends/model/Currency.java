package in.phoenix.myspends.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Charan.Br on 4/10/2017.
 */

@IgnoreExtraProperties
public class Currency {

    private String currencyCode;

    private String currencyName;

    private String currencySymbol;

    public Currency() {

    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }
}
