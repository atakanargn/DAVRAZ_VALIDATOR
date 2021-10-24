package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.Card;

@Database(entities = {Card.class},version = 1)
public abstract class CardDatabase extends RoomDatabase {

    public abstract Card card();

}
