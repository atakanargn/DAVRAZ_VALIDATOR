package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.DeviceCard;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface DeviceCardDao {

    @Query("SELECT * FROM DeviceCard")
    Flowable<List<DeviceCard>> getAll();

    @Insert
    Completable insert(DeviceCard deviceCard);

    @Delete
    Completable delete(DeviceCard deviceCard);

    @Update
    Completable update(DeviceCard deviceCard);



}
