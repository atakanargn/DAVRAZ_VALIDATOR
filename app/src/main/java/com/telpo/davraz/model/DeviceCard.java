package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceCard {



    @ColumnInfo(name = "uid")
    public String uid;
    @ColumnInfo(name = "status")
    public int status;
    @ColumnInfo(name = "user")
    public String user;

    public DeviceCard(String uid, int status, String user) {
        this.uid = uid;
        this.status = status;
        this.user = user;
    }
}
