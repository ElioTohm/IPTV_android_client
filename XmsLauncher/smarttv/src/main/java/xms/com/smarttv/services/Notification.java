package xms.com.smarttv.services;

public class Notification {
    private String[] TYPE = {"Greeting" , "Notification", "pub"};
    private String type;
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
        this.type = this.TYPE[type];
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
