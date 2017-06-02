package in.phoenix.trackmyspends.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Charan.Br on 2/24/2017.
 */

public final class Expense implements Parcelable {

    private int id;

    private ExpenseDate expenseDate;

    private String amount;

    private String note;

    private int paymentTypePriId;

    private String createdOn;

    private String updatedOn;

    public Expense() {

    }

    protected Expense(Parcel in) {
        id = in.readInt();
        expenseDate = in.readParcelable(ExpenseDate.class.getClassLoader());
        amount = in.readString();
        note = in.readString();
        paymentTypePriId = in.readInt();
        createdOn = in.readString();
        updatedOn = in.readString();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ExpenseDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(ExpenseDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getPaymentTypePriId() {
        return paymentTypePriId;
    }

    public void setPaymentTypePriId(int paymentTypePriId) {
        this.paymentTypePriId = paymentTypePriId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    /*public String getAmountInString() {
        return String.valueOf(getAmount());
    }*/

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeParcelable(expenseDate, flags);
        dest.writeString(amount);
        dest.writeString(note);
        dest.writeInt(paymentTypePriId);
        dest.writeString(createdOn);
        dest.writeString(updatedOn);
    }
}
