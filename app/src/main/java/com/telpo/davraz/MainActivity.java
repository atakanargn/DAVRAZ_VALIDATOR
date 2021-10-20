package com.telpo.davraz;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.app.admin.SystemUpdatePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.os.UserManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.common.pos.api.util.PosUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends Activity {
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private TextView txtKartaYazilacak;
    private Button qroku;
    private LinearLayout linearLayout;
    private TextView DateTime,stationID,veriAktarimi;
    private final int red = Color.parseColor("#66FF0000");
    private final int green = Color.parseColor("#6626FF00");
    private final int white = Color.parseColor("#66FFFFFF");
    private int durum = 0;

    private ComponentName mAdminComponentName;
    private DevicePolicyManager mDevicePolicyManager;

    private int sayac;

    private String istasyon_id;

    private String basilanID;

    private String currentDateandTime;

    private ImageView photoView,veriAktarimiSync,ethernet,wifi;
    private static Database myDb;
    private static String ipAdresi, socketPort,apiPort;

    private VideoView video;

    private Tag detectedTag;

    private String gosterilmis="";
    private boolean okutuldu=false;
    private GifImageView arrowGif;

    private final Emitter.Listener relayOpen = new Emitter.Listener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(() -> {
                String gelenID = args[0].toString();
                Log.w("RÖLE","RÖLE AÇIK");
                if(istasyon_id.equals(gelenID)) {
                    linearLayout.getBackground().setTint(green);
                    photoView.setVisibility(View.INVISIBLE);
                    txtKartaYazilacak.setText("Serbest geçiş");
                    arrowGif.setImageResource(R.drawable.arrow);
                    arrowGif.setVisibility(View.VISIBLE);
                    PosUtil.setRelayPower(1);
                    roleKapat();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        linearLayout.getBackground().setTint(white);
                        photoView.setVisibility(View.INVISIBLE);
                        arrowGif.setVisibility(View.INVISIBLE);
                        txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                    }, 500);
                }
            });
        }
    };

    private final Emitter.Listener newUpdate = new Emitter.Listener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void call(final Object... args) {
            MainActivity.this.runOnUiThread(() -> {
                myDb = new Database(MainActivity.this);
                String gelenID = args[0].toString();
                if(istasyon_id.equals(gelenID)) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this).setTitle("Veri Aktarımı")
                            .setMessage("Cihaz elle güncelleme aldı.");
                    final AlertDialog alert = dialog.create();
                    alert.show();
                    final Handler alerthandler  = new Handler();
                    final Runnable runnable = () -> {
                        if (alert.isShowing()) {
                            alert.dismiss();
                        }
                    };
                    alert.setOnDismissListener(dialog1 -> alerthandler.removeCallbacks(runnable));
                    alerthandler.postDelayed(runnable, 2000);

                    String turnikeDelay = (String) args[1].toString();
                    String cardDelay = (String) args[2].toString();

                    myDb.ayarDuzenle("turnikeBekleme", turnikeDelay);
                    myDb.ayarDuzenle("kartBekleme", cardDelay);
                }
            });
        }
    };

    public MainActivity() {
    }

    public void roleKapat() {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> PosUtil.setRelayPower(0), 500);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"SetTextI18n", "UnspecifiedImmutableFlag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULLSCREEN, HIDE NAVIGATION
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_nfc);

        // EĞER UYGULAMA İLK DEFA AÇILIYORSA, VARSAYILAN AYARLAR GELECEKTİR
        myDb = new Database(this);
        try {
            myDb.ayarEkle("turnikeIsim","-");
            myDb.ayarEkle("kurulum","0");
            myDb.ayarEkle("istasyonID", "-");
            myDb.ayarEkle("sunucuIP", "88.255.248.244");
            myDb.ayarEkle("socketPort", "9872");
            myDb.ayarEkle("apiPort", "9091");
            myDb.ayarEkle("turnikeBekleme", "20");
            myDb.ayarEkle("kartBekleme", "480");
        }catch (Exception e){
            //
        }

        PosUtil.setLedPower(0);

        ipAdresi   = "http://"+myDb.ayarGetir("sunucuIP");
        socketPort = myDb.ayarGetir("socketPort");
        apiPort   = myDb.ayarGetir("apiPort");

        if(myDb.ayarGetir("kurulum").equals("0")){
            startActivity(new Intent(MainActivity.this,Settings.class));
        }

        initUI();

        istasyon_id = myDb.ayarGetir("istasyonID");
        stationID.setText(istasyon_id+"\n"+myDb.ayarGetir("turnikeIsim"));

        // NFC AYARLARI
        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();

        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        init_NFC();

        // SOCKETIO AYARLARI
        stationID.setText(istasyon_id+"\n"+myDb.ayarGetir("turnikeIsim"));

        socketeBaglan();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void initUI() {
        video = findViewById(R.id.videoView);
        txtKartaYazilacak = findViewById(R.id.textView);
        linearLayout = findViewById(R.id.myLayout);
        qroku = findViewById(R.id.qroku);
        DateTime = findViewById(R.id.DateTime);
        photoView = findViewById(R.id.photoView);
        GifImageView gifView = findViewById(R.id.dataTransfer_image);
        ethernet = findViewById(R.id.ethernet);
        wifi = findViewById(R.id.wifi);
        stationID = findViewById(R.id.stationID);
        arrowGif = findViewById(R.id.slidingArrow);
        arrowGif.setVisibility(View.INVISIBLE);
        veriAktarimi = findViewById(R.id.veriAktarimi);
        veriAktarimiSync = findViewById(R.id.veriAktarimiSync);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                try{
                    ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    @SuppressLint("MissingPermission") NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    assert nInfo != null;
                    boolean isWiFi = nInfo.getType() == ConnectivityManager.TYPE_WIFI;
                    boolean isEthernet = nInfo.getType() == ConnectivityManager.TYPE_ETHERNET;

                    wifi.setVisibility((isWiFi ?View.VISIBLE:View.INVISIBLE));
                    ethernet.setVisibility((isEthernet ?View.VISIBLE:View.INVISIBLE));
                }catch (Exception e){
                    wifi.setVisibility(View.INVISIBLE);
                    ethernet.setVisibility(View.INVISIBLE);
                }


                currentDateandTime = new SimpleDateFormat("dd-MM-yyyy\nHH:mm:ss").format(new Date());
                DateTime.setText(currentDateandTime);

                if(sayac>=246){
                    String path = "android.resource://" + getPackageName() + "/" + R.raw.video1;
                    video.setVideoURI(Uri.parse(path));
                    video.start();
                    sayac=0;
                }

                if(sayac%15==0) socketeBaglan();

                sayac=sayac+1;

                if(!gosterilmis.equals("")){
                    final Handler gosterilmisTemizle = new Handler(Looper.getMainLooper());
                    gosterilmisTemizle.postDelayed(() -> {gosterilmis="";}, Integer.parseInt(myDb.ayarGetir("kartBekleme"))* 1000L);
                }

                View decorView = getWindow().getDecorView();
                int uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);

                handler.postDelayed(this,1000);
            }
        }, 1000);

        final Handler veriIletimiTimer = new Handler(Looper.getMainLooper());
        veriIletimiTimer.postDelayed(new Runnable() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void run() {
                if(myDb.ayarGetir("kurulum").equals("1")){
                    guncelle();
                }
                veriIletimiTimer.postDelayed(this,10000);
            }
        }, 1000);

        qroku.setOnClickListener(view -> {


            PackageManager packageManager = getPackageManager();
            Intent intent = new Intent();
            intent.setClassName("com.telpo.tps550.api", "com.telpo.tps550.api.barcode.Capture");
            try {
                if (intent.resolveActivityInfo(packageManager, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                    startActivityForResult(intent, 1001);
                } else
                    Toast.makeText(MainActivity.this, "API modle is not install", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ImageView logo = findViewById(R.id.logo);
        logo.setOnClickListener(v -> {
            //Toast.makeText(MainActivity.this,"DEMODA YÖNETİM PANELİ AKTİF DEĞİL!",Toast.LENGTH_LONG).show();
            if(qroku.getVisibility()==View.VISIBLE){
                qroku.setVisibility(View.INVISIBLE);
            }else{
                qroku.setVisibility(View.VISIBLE);
            }
        });

        gifView.setOnClickListener(v -> {
            @SuppressLint("WifiManagerLeak") WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = manager.getConnectionInfo();
            @SuppressLint({"MissingPermission", "HardwareIds"}) String address = info.getMacAddress();

            final AlertDialog.Builder dialog = new AlertDialog.Builder(this).setTitle("Veri Aktarımı")
                    .setMessage("IPv4 : "+getIPAddress()+"\nMAC : "+address+"\nCihaz : "+istasyon_id+"\nVeri aktarımı başlatılıyor.");
            final AlertDialog alert = dialog.create();
            alert.show();

            final Handler alerthandler  = new Handler();
            final Runnable runnable = () -> {
                if (alert.isShowing()) {
                    alert.dismiss();
                }
            };

            alert.setOnDismissListener(dialog1 -> alerthandler.removeCallbacks(runnable));

            alerthandler.postDelayed(runnable, 2000);
            guncelle();
        });

        VideoView video = findViewById(R.id.videoView);
        String path = "android.resource://" + getPackageName() + "/" + R.raw.video1;
        video.setVideoURI(Uri.parse(path));
        video.start();


    }

    public static String getIPAddress() {

        final String[] returned = {""};
        try{
            OkHttpClient client = new OkHttpClient();
            String url = "https://myexternalip.com/raw";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    returned[0] = "255.255.255.0";
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        returned[0] = response.body().string();
                    }else{
                        returned[0] = "255.255.255.0";
                    }
                }
            });
        }catch (Exception e){
            returned[0] = "255.255.255.0";
        }
        return returned[0];
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == 0) {
                if (data != null) {
                    String qrcode = data.getStringExtra("qrCode");
                    if(myDb.qrticketController(qrcode)){
                        try{
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("uid", qrcode);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody body = RequestBody.create(JSON, jsonObject.toString());

                            OkHttpClient client = new OkHttpClient();
                            String url = ipAdresi+":"+apiPort+"/api/qrticket/use/";
                            Request request = new Request.Builder()
                                    .url(url)
                                    .post(body)
                                    .build();
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    // TODO OFFLINE QR EKLEME
                                }
                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) {

                                }
                            });
                        }catch (Exception e){
                            Log.w("Güncelleme",e);
                        }

                        myDb.qrticketUse(qrcode);

                        linearLayout.getBackground().setTint(green);
                        photoView.setVisibility(View.INVISIBLE);
                        txtKartaYazilacak.setText("İyi eğlenceler!");
                        arrowGif.setImageResource(R.drawable.arrow);
                        arrowGif.setVisibility(View.VISIBLE);
                        PosUtil.setRelayPower(1);
                        roleKapat();
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            linearLayout.getBackground().setTint(white);
                            photoView.setVisibility(View.INVISIBLE);
                            arrowGif.setVisibility(View.INVISIBLE);
                            txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                        }, 500);
                    }else{
                        linearLayout.getBackground().setTint(red);
                        photoView.setVisibility(View.INVISIBLE);
                        txtKartaYazilacak.setText("Geçersiz bilet!");
                        arrowGif.setVisibility(View.INVISIBLE);
                        roleKapat();
                        final Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(() -> {
                            linearLayout.getBackground().setTint(white);
                            photoView.setVisibility(View.INVISIBLE);
                            arrowGif.setVisibility(View.INVISIBLE);
                            txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                        }, 500);
                    }

                }
            } else {
                Toast.makeText(MainActivity.this, "Scan Failed", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void guncelle(){
        veriAktarimi.setVisibility(View.VISIBLE);
        tanimKartlarGuncelle();
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
                    runOnUiThread(() -> {
                        veriAktarimi.setText("1/9");
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("1/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {

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
                    runOnUiThread(() -> {
                        veriAktarimi.setText("2/9");
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("2/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {
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
                    runOnUiThread(() -> {
                        veriAktarimi.setText("3/9");
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("3/9");
                    });

                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {
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
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("4/9");
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("4/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {
                            try {
                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    File image = new File("/storage/emulated/0/Pictures/"+uid+".jpg");
                                    if(!image.exists()){
                                        Log.w("image",ipAdresi+":"+apiPort+"/api/user/image/"+uid);
                                        downloadImageNew(uid,ipAdresi+":"+apiPort+"/api/user/image/"+uid);
                                    }
                                }
                                Log.i("Güncelleme", "4 - MÜŞTERİ RESİMLERİ");
                                tarifeGuncelle();
                            } catch (Exception e) {
                                Log.w("Güncelleme",e);
                            }
                        });
                        //offlineKartGuncelle();
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
                    runOnUiThread(() -> {
                        veriAktarimi.setText("5/9");
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("5/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {
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
        runOnUiThread(()->{
            veriAktarimi.setText("6/9");
            veriAktarimi.setVisibility(View.VISIBLE);
            veriAktarimiSync.setVisibility(View.VISIBLE);
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
                            veriAktarimi.setText("6/9");
                            veriAktarimi.setVisibility(View.VISIBLE);
                            veriAktarimiSync.setVisibility(View.VISIBLE);
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
                    runOnUiThread(() -> {
                        veriAktarimi.setText("7/9");
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimiSync.setVisibility(View.VISIBLE);
                    });
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    runOnUiThread(() -> {
                        veriAktarimi.setVisibility(View.VISIBLE);
                        veriAktarimi.setText("7/9");
                    });
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        final String myResponse = response.body().string();
                        MainActivity.this.runOnUiThread(() -> {

                            myDb.deleteAll("yoneticiKartlar");
                            try {
                                String[] parts = myResponse.split(",");
                                for (String uid : parts) {
                                    myDb.yoneticikartEkle(uid);
                                }
                                Log.i("Güncelleme", "7 - YÖNETİCİ KARTLARI");
                                veriAktarimi.setVisibility(View.INVISIBLE);
                                veriAktarimiSync.setVisibility(View.INVISIBLE);
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
    public static void newCardReport(String uid,int onceki,int sonraki,String dateTime,String istasyon,boolean offline,String bid){
        try{
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("uid", uid);
                jsonObject.put("onceki", Integer.toString(onceki));
                jsonObject.put("sonraki", Integer.toString(sonraki));
                jsonObject.put("tarih", dateTime);
                jsonObject.put("istasyon", istasyon);
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                    if(!offline){
                        Log.w("OFFLINE KART","KART EKLENDI.");
                        myDb.offlineCardInsert(uid,onceki,sonraki,dateTime,istasyon);
                    }
                }
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if(offline){
                        myDb.deleteOfflineCard(bid);
                        Log.i("Güncelleme","5 - Okutulmuş Kartlar");
                    }else{
                        Log.i("KART","RAPOR BASILDI.");
                    }

                }
            });
        }catch (Exception e){
            Log.w("Güncelleme",e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    public void onResume() {
        super.onResume();

            if (mNfcAdapter != null) {
                mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    @Override
    public void onNewIntent(@NonNull Intent intent) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            basilanID=ByteArrayToHexString(Objects.<byte[]>requireNonNull(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
            Log.w("OKUTULAN KART",basilanID);

            if(!okutuldu){
                okutuldu=true;
                if(gosterilmis.equals(basilanID)){
                    MediaPlayer gosterilmis_ses = MediaPlayer.create(this, R.raw.gosterilmis);
                    gosterilmis_ses.start();
                    linearLayout.getBackground().setTint(red);
                    txtKartaYazilacak.setText("GÖSTERİLMİŞ KART!");
                    roleKapat();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        linearLayout.getBackground().setTint(white);
                        photoView.setVisibility(View.INVISIBLE);
                        txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                        okutuldu=false;}, Integer.parseInt(myDb.ayarGetir("turnikeBekleme"))* 1000L);
                }else{
                    gosterilmis = basilanID;

                    if(myDb.yoneticiKartController(basilanID)){
                        startActivity(new Intent(MainActivity.this,Settings.class));
                        durum=5;
                        finish();
                    }else {
                        if (myDb.blacklistController(basilanID)) {
                            MediaPlayer gecersiz = MediaPlayer.create(this, R.raw.gecersiz);
                            gecersiz.start();
                            linearLayout.getBackground().setTint(red);
                            txtKartaYazilacak.setText("KARTINIZ KAPATILMIŞTIR!");
                            roleKapat();
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                linearLayout.getBackground().setTint(white);
                                photoView.setVisibility(View.INVISIBLE);
                                txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                                okutuldu=false;
                            }, Integer.parseInt(myDb.ayarGetir("turnikeBekleme")) * 1000L);
                        } else if (myDb.tanimliKartController(basilanID)) {
                            detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                            try {
                                writeTag(buildNdefMessage(processIntent(intent)), detectedTag);
                            } catch (InterruptedException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            if (durum == 1) {
                                MediaPlayer tekli = MediaPlayer.create(this, R.raw.tekli);
                                tekli.start();
                            } else if (durum == -1) {
                                MediaPlayer gecersiz = MediaPlayer.create(this, R.raw.gecersiz);
                                gecersiz.start();
                            } else if (durum == 2) {
                                MediaPlayer yetersiz = MediaPlayer.create(this, R.raw.yetersiz);
                                yetersiz.start();
                            }
                            arrowGif.setImageResource(R.drawable.arrow);
                            arrowGif.setVisibility(View.VISIBLE);
                            roleKapat();
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                linearLayout.getBackground().setTint(white);
                                photoView.setVisibility(View.INVISIBLE);
                                txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                                arrowGif.setVisibility(View.INVISIBLE);
                                okutuldu=false;
                            }, Integer.parseInt(myDb.ayarGetir("turnikeBekleme")) * 1000L);
                        } else {
                            MediaPlayer gecersiz = MediaPlayer.create(this, R.raw.gecersiz);
                            gecersiz.start();
                            linearLayout.getBackground().setTint(red);
                            txtKartaYazilacak.setText("TANIMSIZ KART");
                            roleKapat();
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                linearLayout.getBackground().setTint(white);
                                photoView.setVisibility(View.INVISIBLE);
                                txtKartaYazilacak.setText("Lütfen kartınızı okutunuz");
                                okutuldu=false;
                            }, Integer.parseInt(myDb.ayarGetir("turnikeBekleme")) * 1000L);
                        }
                    }
                }
            }

    }

    private String ByteArrayToHexString(@NonNull byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        StringBuilder out= new StringBuilder();

        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out.append(hex[i]);
            i = in & 0x0f;
            out.append(hex[i]);
        }
        return out.toString();
    }

    public String processIntent(@NonNull Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        assert tag != null;
        NdefMessage[] msgs = getNdefMessagesFromIntent(intent);
        NdefRecord record = msgs[0].getRecords()[0];
        byte[] payload = record.getPayload();
        Log.i("DATA",new String(payload));
        return new String(payload);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
    }

    private void init_NFC() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }


    void writeTag(NdefMessage message, Tag tag)
    {
        int size;
        try {
            size = message.toByteArray().length;
        }catch (Exception ignored){
            return;
        }
        try
        {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null)
            {
                ndef.connect();

                if (!ndef.isWritable())
                {
                    Toast.makeText(MainActivity.this, "Bu kart yazılabilir değil.", Toast.LENGTH_LONG).show();
                    return;
                }

                if (ndef.getMaxSize() < size)
                {
                    Toast.makeText(MainActivity.this,
                            "Kart boyutu desteklenmiyor", Toast.LENGTH_LONG).show();
                    return;
                }

                ndef.writeNdefMessage(message);
            }else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                    } catch (IOException ignored) {
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.w("KART YAZMA",e);
        }

    }

    @SuppressLint("SetTextI18n")
    private NdefMessage buildNdefMessage(String parsedData) throws InterruptedException, UnsupportedEncodingException {
        NdefMessage message = null;
        String yazilacak="";
        String data = basilanID+",KOOP2021,0,sivil";

        Log.w("KART ŞİFRELEME - E",parsedData);
        parsedData = decrypt(parsedData);
        Log.w("KART ŞİFRELEME - D",parsedData);
        if(parsedData.contains("KOOP2021")){
            String[] income = parsedData.split(",");
            String kart_tipii = income[3];
            int bakiye = Integer.parseInt(income[2]);
            int dusecek = myDb.tarifeFee(kart_tipii);
            if(dusecek==-1){
                Log.i("BASIM","GEÇERSİZ 1");
                durum=2;
                txtKartaYazilacak.setVisibility(View.VISIBLE);
                txtKartaYazilacak.setText("GEÇERSİZ KART!");
                linearLayout.getBackground().setTint(red);
                data = basilanID+",KOOP202G,0,G";
                yazilacak = encrypt(data);
                String mimeType = "application/com.telpo.davraz";

                byte[] mimeBytes = mimeType.getBytes(StandardCharsets.UTF_8);
                byte[] dataBytes = yazilacak.getBytes(StandardCharsets.UTF_8);
                byte[] id = new byte[0];

                NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, dataBytes);
                message = new NdefMessage(new NdefRecord[]{record});
                return message;
            }

            String strBakiye = Integer.toString(bakiye);
            if(bakiye<=dusecek){
                Log.i("BASIM","BAKIYE YETERSIZ");
                data = basilanID+",KOOP2021,"+strBakiye+","+kart_tipii;
                durum=2;
                txtKartaYazilacak.setVisibility(View.VISIBLE);
                txtKartaYazilacak.setText("BAKIYE YETERSIZ!");
                linearLayout.getBackground().setTint(red);
            }else{
                Log.i("BASIM","GEÇEBİLİR");
                int yeniBakiye=bakiye-dusecek;
                String strYeniBakiye = Integer.toString(yeniBakiye);
                newCardReport(basilanID,bakiye,yeniBakiye,currentDateandTime,istasyon_id,false,"0");
                txtKartaYazilacak.setVisibility(View.VISIBLE);
                photoView.setImageResource(0);
                File imgFile = new  File("/storage/emulated/0/Pictures/"+basilanID+".jpg");
                if(imgFile.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    photoView.setImageBitmap(myBitmap);
                }
                photoView.setVisibility(View.VISIBLE);
                if(myDb.tarifeFee(kart_tipii)==0){
                    txtKartaYazilacak.setText("KARTINIZI ÇEKEBİLİRSİNİZ\n\nIyi eğlenceler!");
                }else{
                    txtKartaYazilacak.setText("KARTINIZI ÇEKEBİLİRSİNİZ\n\nIyi eğlenceler!\nKALAN BAKIYE : "+ strBakiye);
                }
                linearLayout.getBackground().setTint(green);
                data = basilanID+",KOOP2021,"+ strYeniBakiye +","+kart_tipii;
                PosUtil.setRelayPower(1);
                durum=1;
            }
        }else {
            Log.i("BASIM","GEÇERSİZ 2");
            durum=-1;
            txtKartaYazilacak.setVisibility(View.VISIBLE);
            txtKartaYazilacak.setText("GEÇERSİZ KART!");
            linearLayout.getBackground().setTint(red);
            data = basilanID + ",KOOP202G,0,0";
        }

        yazilacak = encrypt(data);
        String mimeType = "application/com.telpo.davraz";

        byte[] mimeBytes = mimeType.getBytes(StandardCharsets.UTF_8);
        byte[] dataBytes = yazilacak.getBytes(StandardCharsets.UTF_8);
        byte[] id = new byte[0];

        NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, id, dataBytes);
        message = new NdefMessage(new NdefRecord[]{record});
        return message;
    }

    NdefMessage[] getNdefMessagesFromIntent(@NonNull Intent intent)
    {
        // Parse the intent
        NdefMessage[] msgs = null;
        String action = intent.getAction();
        assert action != null;
        if (action.equals(NfcAdapter.ACTION_TAG_DISCOVERED) || action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED))
        {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null)
            {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++)
                {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            }
            else
            {
                // Unknown tag type
                byte[] empty = new byte[]{};
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, empty, empty);
                NdefMessage msg = new NdefMessage(new NdefRecord[]{record});
                msgs = new NdefMessage[]{msg};
            }

        }
        return msgs;
    }

    @Nullable
    public static String encrypt(String value) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            byte[] encode = Base64.encode(encrypted,Base64.NO_WRAP);
            return new String(encode);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    @Nullable
    public static String decrypt(String value) {
        String key = "aesEncryptionKey";
        String initVector = "encryptionIntVec";
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(value,Base64.NO_WRAP));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @SuppressLint("SetTextI18n")
    void socketeBaglan(){
        // SOCKETIO AYARLARI
        istasyon_id = myDb.ayarGetir("istasyonID");
        stationID.setText(istasyon_id+"\n"+myDb.ayarGetir("turnikeIsim"));

        try{
            IO.Options options = new IO.Options();
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .connectTimeout(1000, TimeUnit.MILLISECONDS)
                    .readTimeout(1000, TimeUnit.MILLISECONDS)
                    .writeTimeout( 1000, TimeUnit.MILLISECONDS);
            options.callFactory = clientBuilder.build();

            Socket mSocket = IO.socket(ipAdresi + ":" + socketPort, options);

            if(!mSocket.connected()) {
                mSocket.connect();
                mSocket.on("relayOpen", relayOpen);
                mSocket.on("newUpdate",newUpdate);
                mSocket.emit("sendDevice", istasyon_id);
            }
        }catch (Exception e) {
            Toast.makeText(MainActivity.this,"Cihaz çevrimdışı modda çalışıyor.",Toast.LENGTH_LONG).show();
        }
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
        }catch (Exception ignored){

        }
    }
}


