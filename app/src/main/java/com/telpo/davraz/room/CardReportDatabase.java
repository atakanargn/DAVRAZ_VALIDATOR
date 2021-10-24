package com.telpo.davraz.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.telpo.davraz.model.CardReport;

@Database(entities = {CardReport.class},version = 1)
public abstract class CardReportDatabase extends RoomDatabase {

    public abstract CardReport cardReport();


}
