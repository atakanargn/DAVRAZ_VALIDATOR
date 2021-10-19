package com.telpo.davraz;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.common.pos.api.util.PosUtil;

public class Database extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "database"+Integer.toString(DATABASE_VERSION)+".db";

    private static final String TABLE_NAME = "settings";
    private static String SETTING_NAME  = "name";
    private static String SETTING_VALUE = "value";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSettings        = "CREATE TABLE IF NOT EXISTS settings (name TEXT PRIMARY KEY UNIQUE, value TEXT);";
        String createBlacklist       = "CREATE TABLE IF NOT EXISTS blacklist (UID TEXT UNIQUE, PRIMARY KEY(UID));";
        String createtanimliKartlar  = "CREATE TABLE IF NOT EXISTS tanimliKartlar (UID TEXT UNIQUE, PRIMARY KEY(UID));";

        String createQrTicket        = "CREATE TABLE IF NOT EXISTS qrticket (UID TEXT UNIQUE, STATUS TEXT, PRIMARY KEY(UID));";

        String createOfflineCards    = "CREATE TABLE IF NOT EXISTS offlineCards (bid INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT,uid TEXT, onceki TEXT, sonraki TEXT, tarih TEXT, istasyon TEXT);";
        String createOfflineTickets  = "CREATE TABLE IF NOT EXISTS offlineQrticket (UID TEXT UNIQUE, PRIMARY KEY(UID));";

        String priceSchedule         = "CREATE TABLE IF NOT EXISTS priceSchedule (id INTEGER UNIQUE PRIMARY KEY, ad TEXT, success TEXT, fee INTEGER, ses TEXT);";

        String allCardData           = "CREATE TABLE IF NOT EXISTS cardData (uid TEXT UNIQUE PRIMARY KEY, data TEXT);";

        String createyoneticiKartlar  = "CREATE TABLE IF NOT EXISTS yoneticiKartlar (UID TEXT UNIQUE, PRIMARY KEY(UID));";


        db.execSQL(createSettings);
        db.execSQL(createBlacklist);
        db.execSQL(createtanimliKartlar);
        db.execSQL(createQrTicket);
        db.execSQL(createOfflineCards);
        db.execSQL(createOfflineTickets);
        db.execSQL(priceSchedule);
        db.execSQL(allCardData);
        db.execSQL(createyoneticiKartlar);
    }

    // CARD DATA EKLE
    public void cardBackupEkle(String uid, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cardData WHERE uid='"+uid+"';");
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("data", data);
        db.insert("cardData", null, values);
    }

    public String cardBackupGetir(String uid){
        String value = "";
        String selectQuery = "SELECT data FROM cardData WHERE uid='"+uid+"';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            value = cursor.getString(0);
        }else{
            value = "-1";
        }

        cursor.close();
        return value;
    }

    // AYAR EKLEME
    public void ayarEkle(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("value", value);

        Cursor cursor = db.rawQuery("SELECT * FROM settings;", null);

        db.insert("settings", null, values);
        ;
    }

    // AYARLARIN İÇERİSİNDEN İSTENEN AYARI ÇEKME
    public String ayarGetir(String name){
        String value = "";
        String selectQuery = "SELECT * FROM settings WHERE name='"+name+"';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            value = cursor.getString(1);
        }else{
            value = "-1";
        }

        cursor.close();
        ;
        return value;
    }

    // AYAR DEĞERLERİNİ DEĞİŞTİRME
    public void ayarDuzenle(String name, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("value", value);

        db.update("settings", values, "name = ?", new String[] { String.valueOf(name) });
        ;
    }

    // KARALİSTEYE KART EKLEME
    public boolean blacklistInsert(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID",uid);
        long result=-1;
        try{
            result = db.insert("blacklist",null,contentValues);
        }catch (Exception e){
            //
        }
        if(result==-1){
            ;
            return false;
        }else{
            ;
            return true;
        }
    }

    // KARALİSTEDEN KART DURUMU SORGUSU
    public boolean blacklistController(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM blacklist WHERE UID='"+uid+"';",null);
        int var = res.getCount();
        //Log.w("KARALISTE",Integer.toString(var));
        if(var==1){
            ;
            return true;
        }else{
            ;
            return false;
        }
    }

    // Tablodaki verileri silme
    public void deleteAll(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+table+";");
        ;
    }

    // TANIMLI KARTLARI EKLEME
    public boolean kartEkle(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID",uid);
        long result=-1;
        try{
            result = db.insert("tanimliKartlar",null,contentValues);
        }catch (Exception e){
            //
        }

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    // TANIMLI KART DURUMU SORGUSU
    public boolean tanimliKartController(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM tanimliKartlar WHERE UID='"+uid+"';",null);
        int var = res.getCount();
        //Log.w("TANIMLI KART SAYI",Integer.toString(var));
        if(var==1){
            ;
            return true;
        }else{
            ;
            return false;
        }
    }


    // TANIMLI KARTLARI EKLEME
    public boolean yoneticikartEkle(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID",uid);
        long result=-1;
        try{
            result = db.insert("yoneticiKartlar",null,contentValues);
        }catch (Exception e){
            //
        }

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    // TANIMLI KART DURUMU SORGUSU
    public boolean yoneticiKartController(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM yoneticiKartlar WHERE UID='"+uid+"';",null);
        int var = res.getCount();
        //Log.w("TANIMLI KART SAYI",Integer.toString(var));
        if(var==1){
            ;
            return true;
        }else{
            ;
            return false;
        }
    }

    // QR BİLETLERİ EKLEME
    public boolean qrticketInsert(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID",uid);
        contentValues.put("STATUS","1");
        long result=-1;
        try{
            result = db.insert("qrticket",null,contentValues);
        }catch (Exception e){
            //
        }
        if(result==-1){
            ;
            return false;
        }else{
            ;
            return true;
        }
    }

    public boolean qrticketController(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM qrticket WHERE UID='"+uid+"' AND STATUS='1';",null);
        int var = res.getCount();
        if(var>=1){
            ;
            return true;
        }else{
            ;
            return false;
        }
    }

    public void qrticketUse(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        String querym = "DELETE FROM qrticket WHERE UID='"+uid+"';";
        db.execSQL(querym);
        ;
    }

    public boolean offlineCardInsert(String uid,int onceki,int sonraki,String dateTime,String istasyon){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid",uid);
        contentValues.put("onceki",onceki);
        contentValues.put("sonraki",sonraki);
        contentValues.put("tarih",dateTime);
        contentValues.put("istasyon",istasyon);
        long result=-1;
        try{
            result = db.insert("offlineCards",null,contentValues);
        }catch (Exception e){
            //
        }
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public void deleteOfflineCard(String bid){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM offlineCards WHERE bid="+bid+";");
    }

    public String offlineCardSend(int id){
        String value = "";
        String selectQuery = "SELECT * FROM offlineCards;";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > id) {
            cursor.moveToPosition(id);
            String uid = cursor.getString(1);
            String onceki = cursor.getString(2);
            String sonraki = cursor.getString(3);
            String tarih = cursor.getString(4);
            String istasyon = cursor.getString(5);
            String bid = Integer.toString(cursor.getInt(0));

            value = uid+","+onceki+","+sonraki+","+tarih+","+istasyon+","+bid;
        } else {
            value = "-1";
        }

        cursor.close();
        db.close();
        return value;
    }

    public boolean offlineQrticketInsert(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uid",uid);
        long result=-1;
        try{
            result = db.insert("offlineQrticket",null,contentValues);
        }catch (Exception e){
            //
        }
        if(result==-1){
            ;
            return false;
        }else{
            ;
            return true;
        }
    }

    // Tarifeler
    public boolean tarifeEkle(int id, String ad, String success, String fee, String ses){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id",id);
        contentValues.put("ad",ad);
        contentValues.put("success",success);
        contentValues.put("fee",fee);
        contentValues.put("ses",ses);
        long result=-1;
        try{
            result = db.insert("priceSchedule",null,contentValues);
        }catch (Exception e){
            //
        }

        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    // Tarife Fee
    @SuppressLint("Range")
    public int tarifeFee(String tip){
        int value = -1;
        String selectQuery = String.format("SELECT ad,fee FROM priceSchedule;", tip);
        Log.i("q",selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        while(cursor.moveToNext()){
            Log.i("d",cursor.getString(0));
            if(cursor.getString(0).equals(tip)){
                value= cursor.getInt(1);
                Log.i("FEE",Integer.toString(value));
                break;
            }
        }

        cursor.close();
        return value;
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }

}