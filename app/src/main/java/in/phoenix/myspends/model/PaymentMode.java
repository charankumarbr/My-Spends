package in.phoenix.myspends.model;

import java.util.ArrayList;

import in.phoenix.myspends.database.DBConstants;

/**
 * Created by Charan.Br on 2/24/2017.
 */

public final class PaymentMode {

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ArrayList<PaymentMode> getPaymentModes() {

        ArrayList<PaymentMode> allModes = new ArrayList<>();
        PaymentMode debitCardMode = new PaymentMode();
        debitCardMode.setName(DBConstants.PAYMENT_MODE.DEBIT_CARD);
        debitCardMode.setId(DBConstants.PAYMENT_MODE.DEBIT_CARD_ID);

        PaymentMode creditCardMode = new PaymentMode();
        creditCardMode.setName(DBConstants.PAYMENT_MODE.CREDIT_CARD);
        creditCardMode.setId(DBConstants.PAYMENT_MODE.CREDIT_CARD_ID);

        PaymentMode walletMode = new PaymentMode();
        walletMode.setName(DBConstants.PAYMENT_MODE.WALLET);
        walletMode.setId(DBConstants.PAYMENT_MODE.WALLET_ID);

        PaymentMode netBankingMode = new PaymentMode();
        netBankingMode.setName(DBConstants.PAYMENT_MODE.NET_BANKING);
        netBankingMode.setId(DBConstants.PAYMENT_MODE.NET_BANKING_ID);

        allModes.add(debitCardMode);
        allModes.add(creditCardMode);
        allModes.add(walletMode);
        allModes.add(netBankingMode);

        return allModes;
    }

    public static String getModeName(int paymentModeId) {
        switch (paymentModeId) {
            case DBConstants.PAYMENT_MODE.CREDIT_CARD_ID:
                return DBConstants.PAYMENT_MODE.CREDIT_CARD;

            case DBConstants.PAYMENT_MODE.DEBIT_CARD_ID:
                return DBConstants.PAYMENT_MODE.DEBIT_CARD;

            case DBConstants.PAYMENT_MODE.WALLET_ID:
                return DBConstants.PAYMENT_MODE.WALLET;

            case DBConstants.PAYMENT_MODE.NET_BANKING_ID:
                return DBConstants.PAYMENT_MODE.NET_BANKING;
        }

        return "";
    }
}
