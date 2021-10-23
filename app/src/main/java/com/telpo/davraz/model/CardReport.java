package com.telpo.davraz.model;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CardReport {



    @ColumnInfo(name = "uid")
    public String uid;
    @ColumnInfo(name = "istasyon")
    public String istasyon;
    @ColumnInfo(name = "onceki")
    public int onceki;
    @ColumnInfo(name = "sonraki")
    public int sonraki;
    @ColumnInfo(name = "tarih")
    public String tarih;
    @ColumnInfo(name = "photo")
    public String photo;

    public CardReport(String uid, String istasyon, int onceki, int sonraki, String tarih, String photo) {
        this.uid = uid;
        this.istasyon = istasyon;
        this.onceki = onceki;
        this.sonraki = sonraki;
        this.tarih = tarih;
        this.photo = photo;
    }
}
