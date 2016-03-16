package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by PiratePowWow on 3/15/16.
 */
@Entity
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    int id;
    @ManyToOne
    User sender;
    @ManyToOne
    User recipient;
    @Column(nullable = false)
    String filename;
    @Column(nullable = false)
    String timeStamp;
    @Column(nullable = false)
    int duration;

    public Photo() {
    }

    public Photo(User sender, User recipient, String filename, String timeStamp, int duration) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
        this.timeStamp = timeStamp;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
