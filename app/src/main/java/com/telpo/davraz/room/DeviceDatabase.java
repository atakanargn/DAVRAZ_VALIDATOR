package com.telpo.davraz.room;

import androidx.room.Database;

import com.telpo.davraz.model.Device;

@Database(entities = {Device.class},version = 1)
public abstract class DeviceDatabase {

    public abstract Device device();

}
