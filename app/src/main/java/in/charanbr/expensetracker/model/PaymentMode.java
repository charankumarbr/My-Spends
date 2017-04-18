package in.charanbr.expensetracker.model;

/**
 * Created by Charan.Br on 2/24/2017.
 */

public final class PaymentMode {

    private int id;

    private String name;

    private int modeId;

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

    public int getModeId() {
        return modeId;
    }

    public void setModeId(int modeId) {
        this.modeId = modeId;
    }
}
