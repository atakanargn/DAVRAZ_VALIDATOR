package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Device {



    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "type")
    public String type;
    @ColumnInfo(name = "stationTag")
    public String stationTag;
    @ColumnInfo(name = "stationId")
    public String stationId;
    @ColumnInfo(name = "turnikeDelay")
    public String turnikeDelay;
    @ColumnInfo(name = "cardDelay")
    public String cardDelay;
    @ColumnInfo(name = "qrNum")
    public  int qrNum;
    @ColumnInfo(name = "version")
    public  int version;



}
