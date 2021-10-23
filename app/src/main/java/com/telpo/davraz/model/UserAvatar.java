package com.telpo.davraz.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserAvatar {


    
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "version")
    public int version;




}
