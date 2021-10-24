package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.PriceShedule;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;


@Dao
public interface PriceSheduleDao {

    @Query("SELECT * FROM PriceShedule")
    Flowable<List<PriceShedule>> getAll();

    @Insert
    Completable insert(PriceShedule priceShedule);

    @Delete
    Completable delete(PriceShedule priceShedule);

    @Update
    Completable update(PriceShedule priceShedule);



}
