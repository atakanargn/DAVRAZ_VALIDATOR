package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Device {




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

    public Device(String type, String stationTag, String stationId, String turnikeDelay, String cardDelay, int qrNum, int version) {
        this.type = type;
        this.stationTag = stationTag;
        this.stationId = stationId;
        this.turnikeDelay = turnikeDelay;
        this.cardDelay = cardDelay;
        this.qrNum = qrNum;
        this.version = version;
    }
}
