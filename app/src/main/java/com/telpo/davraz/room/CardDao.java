package com.telpo.davraz.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


import com.telpo.davraz.model.Card;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface CardDao {
    @Query("SELECT * FROM Card")
    Flowable<List<Card>> getAll();

    @Insert
    Completable insert(Card card);

    @Delete
    Completable delete(Card card);

    @Update
    Completable update(Card card);
}
