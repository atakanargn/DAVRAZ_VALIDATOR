package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.BlackList;

@Database(entities = {BlackList.class},version = 1)
public abstract class BlackListDatabase extends RoomDatabase {

    public abstract BlackList blackList();


}
