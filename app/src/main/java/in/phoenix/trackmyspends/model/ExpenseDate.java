package in.phoenix.trackmyspends.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

import in.phoenix.trackmyspends.util.AppUtil;

/**
 * Created by Charan.Br on 1/31/2017.
 */
public class ExpenseDate implements Parcelable {

    protected ExpenseDate(Parcel in) {
        dayOfMonth = in.readInt();
        month = in.readInt();
        year = in.readInt();
    }

    public ExpenseDate(String expenseDate) {
        changeDate(expenseDate);
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * 0 to 11, zero based
     *
     * @return Month number
     */
    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    private int dayOfMonth;
    private int month;
    private int year;

    public static final Creator<ExpenseDate> CREATOR = new Creator<ExpenseDate>() {
        @Override
        public ExpenseDate createFromParcel(Parcel in) {
            return new ExpenseDate(in);
        }

        @Override
        public ExpenseDate[] newArray(int size) {
            return new ExpenseDate[size];
        }
    };

    public ExpenseDate(int dayOfMonth, int month, int year) {
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.year = year;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dayOfMonth);
        dest.writeInt(month);
        dest.writeInt(year);
    }

    public String getDisplayableDate() {
        return getDayOfMonth() + " " + AppUtil.getShortMonth(getMonth()) + " " + getYear();
    }

    public String getFormattedDate() {
        return AppUtil.getThDate(dayOfMonth) + " " + AppUtil.getShortMonth(getMonth()) + " " + getYear();
    }

    public long getTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTimeInMillis();
    }

    public long reportToDateTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        calendar.add(Calendar.MONTH, 3);
        return calendar.getTimeInMillis();
    }

    @Override
    public String toString() {
        return getDayOfMonth() + "|" + getMonth() + "|" + getYear();
    }

    public boolean isSameExpenseDate(String otherDate) {

        StringBuilder expenseDateBuilder = new StringBuilder();
        if (getDayOfMonth() < 10) {
            expenseDateBuilder.append("0");
        }
        expenseDateBuilder.append(getDayOfMonth());
        expenseDateBuilder.append("-");
        if (getMonth() < 10) {
            expenseDateBuilder.append("0");
        }
        expenseDateBuilder.append(getMonth() + 1);
        expenseDateBuilder.append("-");
        expenseDateBuilder.append(getYear());

        String[] otherDateParts = otherDate.split(" ");
        return !otherDateParts[0].equals(expenseDateBuilder.toString());

    }

    public void changeDate(String expenseDate) {
        if (expenseDate.contains("|")) {
            String[] splits = expenseDate.split("\\|");
            dayOfMonth = Integer.parseInt(splits[0]);
            month = Integer.parseInt(splits[1]);
            year = Integer.parseInt(splits[2]);

        } else {
            throw new IllegalArgumentException("Invalid Expense Date");
        }
    }
}
