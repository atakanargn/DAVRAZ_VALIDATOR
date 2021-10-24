package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.telpo.davraz.model.CardReport;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface CardReportDao {

    @Query("SELECT * FROM CardReport")
    Flowable<List<CardReport>> getAll();

    @Insert
    Completable insert(CardReport cardReport);

    @Delete
    Completable delete(CardReport cardReport);

    @Update
    Completable update(CardReport cardReport);


}
