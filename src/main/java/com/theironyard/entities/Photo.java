package com.theironyard.entities;

import javax.persistence.*;

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

    public Photo() {
    }

    public Photo(User sender, User recipient, String filename) {
        this.sender = sender;
        this.recipient = recipient;
        this.filename = filename;
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
