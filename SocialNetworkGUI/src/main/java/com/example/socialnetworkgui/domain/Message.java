package com.example.socialnetworkgui.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long> {
    Long from;
    List<Long> to;
    String message;
    LocalDateTime date;
    Long reply;

    public Message(Long from, List<Long> to, LocalDateTime date, String message, Long reply) {
        this.from = from;
        this.to = to;
        this.date = date;
        this.message = message;
        this.reply = reply;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public void setReply(Long reply) {
        this.reply = reply;
    }

    public Long getReply() {
        return reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date) && Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, message, date, reply);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from=" + from +
                ", to=" + to +
                ", message='" + message + '\'' +
                ", date=" + date +
                '}';
    }
}
