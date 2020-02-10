package com.derich.matatufy;

import java.util.Map;

public class Chats {
    public String userEmail;
    public String message;
    public Map<String, Object> timestamp;

    public Chats(){

    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> getTimestamp() {
        return timestamp;
    }

    public Chats(String userEmail, String message, Map<String, Object> timeS) {
        this.userEmail = userEmail;
        this.message = message;
        this.timestamp = timeS;
    }
}
