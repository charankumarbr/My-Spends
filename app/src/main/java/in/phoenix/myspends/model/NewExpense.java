package in.phoenix.myspends.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * Created by Charan.Br on 11/26/2017.
 */

@IgnoreExtraProperties
public final class NewExpense implements Parcelable {

    private String id;

    private long expenseDate;

    private Float amount;

    private String note;

    private String paymentTypeKey;

    private long createdOn;

    private long updatedOn;

    private int categoryId;

    public NewExpense() {

    }

    protected NewExpense(Parcel in) {
        id = in.readString();
        expenseDate = in.readLong();
        amount = in.readFloat();
        note = in.readString();
        paymentTypeKey = in.readString();
        createdOn = in.readLong();
        updatedOn = in.readLong();
        categoryId = in.readInt();
    }

    public static final Creator<NewExpense> CREATOR = new Creator<NewExpense>() {
        @Override
        public NewExpense createFromParcel(Parcel in) {
            return new NewExpense(in);
        }

        @Override
        public NewExpense[] newArray(int size) {
            return new NewExpense[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(long expenseDate) {
        this.expenseDate = expenseDate;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPaymentTypeKey() {
        return paymentTypeKey;
    }

    public void setPaymentTypeKey(String paymentTypeKey) {
        this.paymentTypeKey = paymentTypeKey;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public long getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(long updatedOn) {
        this.updatedOn = updatedOn;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeLong(expenseDate);
        parcel.writeFloat(amount);
        parcel.writeString(note);
        parcel.writeString(paymentTypeKey);
        parcel.writeLong(createdOn);
        parcel.writeLong(updatedOn);
        parcel.writeInt(categoryId);
    }

    @Override
    public String toString() {
        return getId() + ":" + getNote()
                + ":" + getAmount() + ":" + getCreatedOn() + ":"
                + getExpenseDate() + ":" + getPaymentTypeKey() + ":" + getUpdatedOn() + ":" + getCategoryId();
    }

    public boolean isAddedOnDiffDate() {
        return new ExpenseDate(getExpenseDate()).isSameExpenseDate(getCreatedOn());
    }

    public boolean isUpdated() {
        return getCreatedOn() != getUpdatedOn();
    }

}
