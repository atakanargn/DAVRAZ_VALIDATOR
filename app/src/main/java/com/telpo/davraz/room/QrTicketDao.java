package com.telpo.davraz.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.telpo.davraz.model.QrTicket;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface QrTicketDao {

    @Query("SELECT * FROM QrTicket")
    Flowable<List<QrTicket>> getAll();

    @Insert
    Completable insert(QrTicket qrTicket);

    @Delete
    Completable delete(QrTicket qrTicket);

    @Update
    Completable update(QrTicket qrTicket);

}
