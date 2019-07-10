package ru.pon.demo.model;

public class NewMessage {
    private String text;

    public NewMessage(String text) {
        this.text = text;
    }

    public NewMessage() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
