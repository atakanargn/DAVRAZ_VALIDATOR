package com.telpo.davraz.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class BlackList {

    @ColumnInfo(name = "uid")
    public String uid;

}
