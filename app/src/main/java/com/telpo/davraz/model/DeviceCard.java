package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceCard {



    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "status")
    public int status;
    @ColumnInfo(name = "user")
    public String user;


}