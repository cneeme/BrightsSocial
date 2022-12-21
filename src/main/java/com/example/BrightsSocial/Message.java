package com.example.BrightsSocial;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Message {
    private String messageBody;
    private String sender;
    private LocalTime now;

    private LocalDateTime time;
    private String date;

    public Message(String messageBody, String sender, LocalDateTime time) {
        this.messageBody = messageBody;
        this.sender = sender;
        this.time = time;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getSender() {
        return sender;
    }

    public String getDate(){
        return date;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTime() {
        String date = time.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"));
        return date;
    }



    @Override
    public String toString() {
        return "Message{" +
                "messageBody='" + messageBody + '\'' +
                ", sender='" + sender + '\'' +
                ", time=" + getTime() +
                '}';
    }
}
