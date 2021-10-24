package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.PriceShedule;


@Database(entities = {PriceShedule.class},version = 1)
public abstract class PriceSheduleDatabase extends RoomDatabase {

    public abstract PriceShedule priceShedule();



}
