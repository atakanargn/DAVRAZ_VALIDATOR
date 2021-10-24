package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.DeviceCard;

@Database(entities = {DeviceCard.class},version = 1)
public abstract class DeviceCardDatabase extends RoomDatabase {

   public abstract DeviceCard deviceCard();

}
