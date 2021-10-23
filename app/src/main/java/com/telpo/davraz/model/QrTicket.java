package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QrTicket {



    @ColumnInfo(name = "uid")
    public String uid;
    @ColumnInfo(name = "status")
    public String status;
    @ColumnInfo(name = "creator")
    public String creator;
    @ColumnInfo(name = "user")
    public String user;
    @ColumnInfo(name = "tarih")
    public String tarih;

    public QrTicket(String uid, String status, String creator, String user, String tarih) {
        this.uid = uid;
        this.status = status;
        this.creator = creator;
        this.user = user;
        this.tarih = tarih;
    }
}
