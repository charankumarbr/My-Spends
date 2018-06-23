package in.phoenix.myspends.model;

import com.google.firebase.database.Exclude;

/**
 * Created by Charan.Br on 4/10/2018.
 */

public final class MessageBoard {

    private String message;

    @Exclude
    private String key;

    private long createdOn;

    private long updatedOn;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public String toString() {
        return message + ":" + key + ":" + createdOn + ":" + updatedOn;
    }
}