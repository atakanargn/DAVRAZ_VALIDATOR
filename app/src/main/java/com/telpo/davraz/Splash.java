package com.telpo.davraz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.common.pos.api.util.PosUtil;
import com.common.pos.api.util.ShellUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Splash extends Activity {

    private Database myDb;
    private static final String KIOSK_PACKAGE = "com.telpo.davraz";
    private static final String[] APP_PACKAGES = {KIOSK_PACKAGE};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_splash);

        PosUtil.setRelayPower(0);

        String cmd = "echo 5 > /sys/class/backlight/led-brightness/brightness";//新
        ShellUtils.execCommand(cmd, false);

        // DATABASE
        myDb = new Database(this);
        try{
            // BLACKLIST TEST
            OkHttpClient client = new OkHttpClient();
            String url = "http://34.133.75.203:9091/api/blacklist";
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    // Toast.makeText(Splash.this,"Sunucuyla bağlantı kurulamadı.",Toast.LENGTH_LONG).show();
                    allDone();
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String[] uid = new String[1];
                    if (response.isSuccessful()) {
                        final String myResponse = response.body().string();
                        Splash.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    myDb.deleteAll("blacklist");
                                    PosUtil.setLedPower(1);
                                    JSONArray mJsonArray = new JSONArray(myResponse);
                                    for(int i=0;i<mJsonArray.length();i++){
                                        JSONObject mJsonObject = mJsonArray.getJSONObject(i);
                                        String uid = mJsonObject.getString("uid");
                                        myDb.blacklistInsert(uid);
                                    }
                                    PosUtil.setLedPower(0);
                                    allDone();
                                } catch (JSONException e) {
                                    // Toast.makeText(Splash.this,"Sunucuyla bağlantı kurulamadı.",Toast.LENGTH_LONG).show();
                                    allDone();
                                }
                            }
                        });
                    }
                }
            });
        }catch (Exception e){
            // Toast.makeText(Splash.this,"Sunucuyla bağlantı kurulamadı.",Toast.LENGTH_LONG).show();
            allDone();
        }
    }

    protected void allDone(){
        PosUtil.setLedPower(0);
        startActivity(new Intent(Splash.this,MainActivity.class));
    }

}
