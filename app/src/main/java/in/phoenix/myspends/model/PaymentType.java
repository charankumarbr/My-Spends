package in.phoenix.myspends.model;

import in.phoenix.myspends.database.DBConstants;

/**
 * Created by Charan.Br on 2/11/2017.
 */

public final class PaymentType {

    public PaymentType() {

    }

    private String key;

    private int paymentModeId;

    private String name;

    private long createdOn;

    private boolean isActive;

    public int getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(int paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public static PaymentType getCashPaymentType() {

        PaymentType cashPaymentType = new PaymentType();
        cashPaymentType.setName(DBConstants.PAYMENT_MODE.CASH);
        cashPaymentType.setPaymentModeId(DBConstants.PAYMENT_MODE.CASH_ID);
        cashPaymentType.setCreatedOn(0);
        cashPaymentType.setActive(true); //-- deemed active while inserting --//
        cashPaymentType.setKey("0");

        return cashPaymentType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static boolean isCashType(long createdOn, int paymentModeId) {
        return (createdOn == 0 && paymentModeId == DBConstants.PAYMENT_MODE.CASH_ID);
    }
}
