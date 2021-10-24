package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.BlackList;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface BlackListDao {

    @Query("SELECT * FROM BlackList")
    Flowable<List<BlackList>> getAll();

    @Insert
    Completable insert(BlackList blackList);

    @Delete
    Completable delete(BlackList blackList);

    @Update
    Completable update(BlackList blackList);


}
