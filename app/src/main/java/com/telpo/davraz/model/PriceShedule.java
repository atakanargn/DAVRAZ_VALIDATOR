package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PriceShedule {




    @ColumnInfo(name = "device")
    public String device;
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo (name = "success")
    public String success;
    @ColumnInfo(name = "ses")
    public String ses;
    @ColumnInfo(name = "fee")
    public int fee;


}
