package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.Device;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface DeviceDao {

    @Query("SELECT * FROM Device")
    Flowable<List<Device>> getAll();

    @Insert
    Completable insert(Device device);

    @Delete
    Completable delete(Device device);

    @Update
    Completable update(Device device);



}
