package xms.com.smarttv.services;

public class Notification {
    private int type;
    private String message;
    private String image;

    public Notification() {}

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
