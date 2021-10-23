package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class QrTicket {



    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "status")
    public String status;
    @ColumnInfo(name = "creator")
    public String creator;
    @ColumnInfo(name = "user")
    public String user;
    @ColumnInfo(name = "tarih")
    public String tarih;

}
