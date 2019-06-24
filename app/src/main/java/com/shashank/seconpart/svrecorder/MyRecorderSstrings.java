package com.shashank.seconpart.svrecorder;

public class MyRecorderSstrings {

    String email;
    String name;
    String URL;
    String message;

    public MyRecorderSstrings() {
    }
    public MyRecorderSstrings(String email, String name, String URL) {
        this.email = email;
        this.name = name;
        this.URL = URL;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setURL(String URL) {
        this.URL = URL;
    }
    public String getEmail() {
        return email;
    }
    public String getMessage() {
        return message;
    }
    public String getName() {
        return name;
    }
    public String getURL() {
        return URL;
    }
}
