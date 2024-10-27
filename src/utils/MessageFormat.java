package utils;

import java.io.Serializable;

public class MessageFormat implements Serializable {

    private static final long serialVersionUID = 1L;
    private String sender;
    private String receiver;
    private String message;

    public MessageFormat() {
    }

    public MessageFormat(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
