package com.telpo.davraz;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.pos.api.util.PosUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Attr;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;

public class Guncelle extends MainActivity{
    public Context context;
    public Database myDb;
    public String ipAdresi;
    public String apiPort;
    public String socketPort;
    public String istasyon_id;
    public MainActivity activity;
    public TextView veriAktarimi;
    public ImageView veriAktarimiSync;
    public MainActivity ref;

    public Guncelle(Database myDb, MainActivity activity) {
        this.myDb = myDb;
        this.ipAdresi = "http://"+myDb.ayarGetir("sunucuIP");;
        this.apiPort = myDb.ayarGetir("apiPort");
        this.socketPort = myDb.ayarGetir("socketPort");
        this.istasyon_id = myDb.ayarGetir("istasyonID");
        this.activity = activity;

    }



    public void tanimKartlarGuncelle(){

        try{
            OkHttpClient client = new OkHttpClient();

            String url = ipAdresi+":"+apiPort+"/api/card/tanimli";

            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {

                        activity.veriAktarimi.setText("1/9");
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("1/9");

                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();

                        activity.runOnUiThread(() -> {

                            myDb.deleteAll("tanimliKartlar");
                            try {

                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    myDb.kartEkle(uid);
                                }
                                Log.i("Güncelleme", "1 - TANIMLI KARTLAR");
                                qrTicketGuncelleme();
                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    public void qrTicketGuncelleme(){
        try{
            OkHttpClient client = new OkHttpClient();
            String url = ipAdresi+":"+apiPort+"/api/qrticket/all/1";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setText("2/9");
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("2/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        activity.runOnUiThread(() -> {
                            try {
                                myDb.deleteAll("qrticket");

                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    myDb.qrticketInsert(uid);
                                }

                                Log.i("Güncelleme", "2 - QR BİLET");
                                blacklistGuncelle();
                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    public void blacklistGuncelle(){
        try{
            // BLACKLIST TEST
            OkHttpClient client = new OkHttpClient();
            String url = ipAdresi+":"+apiPort+"/api/card/2";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setText("3/9");
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("3/9");
                    });

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        activity.runOnUiThread(() -> {
                            try {
                                myDb.deleteAll("blacklist");

                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    myDb.blacklistInsert(uid);
                                }

                                Log.i("Güncelleme", "3 - KARALİSTE");


                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }

                        });

                        fotografGuncelle();

                        //offlineKartGuncelle();
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    public void fotografGuncelle(){
        try{
            OkHttpClient client = new OkHttpClient();
            String url = ipAdresi+":"+apiPort+"/api/user/imagelist";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("4/9");
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("4/9");

                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;

                        final String myResponse = response.body().string();
                        activity.runOnUiThread(() -> {
                            try {

                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    File image = new File("/storage/emulated/0/Pictures/"+uid+".jpg");
                                    if(!image.exists()){
                                        Log.w("image",ipAdresi+":"+apiPort+"/api/user/image/"+uid);
                                        ref.downloadImageNew(uid,ipAdresi+":"+apiPort+"/api/user/image/"+uid);
                                    }
                                }
                                Log.i("Güncelleme", "4 - MÜŞTERİ RESİMLERİ");
                                //tarifeGuncelle();
                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                        //offlineKartGuncelle();
                        tarifeGuncelle();
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    public void tarifeGuncelle(){
        try{

            OkHttpClient client = new OkHttpClient();
            String url = ipAdresi+":"+apiPort+"/api/pricing/"+istasyon_id;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setText("5/9");
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("5/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        activity.runOnUiThread(() -> {
                            try {
                                myDb.deleteAll("priceSchedule");
                                String[] parts = myResponse.split("-");
                                int i=0;
                                for (String tarife : parts) {
                                    //Log.i("tarife",tarife);
                                    i=i+1;
                                    String[] ayar = tarife.split(",");
                                    myDb.tarifeEkle(i,ayar[0],"MESAJ",ayar[1],"tekli");
                                }
                                Log.i("Güncelleme", "5 - TARIFELER");
                                offlineKartGuncelle();

                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    public void offlineKartGuncelle(){
        activity.runOnUiThread(()->{
            activity.veriAktarimi.setText("6/9");
            activity.veriAktarimi.setVisibility(View.VISIBLE);
            activity.veriAktarimiSync.setVisibility(View.VISIBLE);
        });

        final int[] sayac = {0,-1};

        String gelen = myDb.offlineCardSend(sayac[0]);
        while(!gelen.equals("-1")){
            gelen = myDb.offlineCardSend(sayac[0]);
            if(gelen.equals("-1")){
                break;
            }
            String[] bilgiler = gelen.split(",");

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", bilgiler[0]);
                jsonObject.put("onceki", bilgiler[1]);
                jsonObject.put("sonraki", bilgiler[2]);
                jsonObject.put("tarih", bilgiler[3]);
                jsonObject.put("istasyon", bilgiler[4]);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try{
                if(sayac[0]!=sayac[1]){
                    sayac[0]=sayac[1];
                    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                    RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                    OkHttpClient client = new OkHttpClient();
                    String url = ipAdresi+":"+apiPort+"/api/newcard/";
                    Request request = new Request.Builder()
                            .url(url)
                            .post(body)
                            .build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            activity.veriAktarimi.setText("6/9");
                            activity.veriAktarimi.setVisibility(View.VISIBLE);
                            activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            myDb.deleteOfflineCard(bilgiler[5]);
                            sayac[0]++;
                        }
                    });
                }
            }catch (Exception e){
                Log.w("Güncelleme",e);
            }
        }

        Log.i("Güncelleme","6 - Offline Kartlar");
        yoneticiKartlarGuncelle();
    }

    public void yoneticiKartlarGuncelle(){
        try{
            OkHttpClient client = new OkHttpClient();
            String url = ipAdresi+":"+apiPort+"/api/devcard/";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setText("7/9");
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    activity.runOnUiThread(() -> {
                        activity.veriAktarimi.setVisibility(View.VISIBLE);
                        activity.veriAktarimi.setText("7/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        activity.runOnUiThread(() -> {

                            myDb.deleteAll("yoneticiKartlar");
                            try {
                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    myDb.yoneticikartEkle(uid);
                                }

                                Log.i("Güncelleme", "7 - YÖNETİCİ KARTLARI");
                                activity.veriAktarimi.setVisibility(View.INVISIBLE);
                                activity.veriAktarimiSync.setVisibility(View.INVISIBLE);
                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

}
