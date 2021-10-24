package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.QrTicket;

@Database(entities = {QrTicket.class},version = 1)
public abstract class QrTicketDatabase extends RoomDatabase {


    public abstract QrTicket qrTicket();




}
