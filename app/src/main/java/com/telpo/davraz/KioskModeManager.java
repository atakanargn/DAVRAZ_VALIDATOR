package com.telpo.davraz;

import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.util.Log;

import java.util.logging.Logger;

public class KioskModeManager {
    private Activity activity;
    private DevicePolicyManager devicePolicyManager;        // Device policy manager for activate Device admin
    private ComponentName mAdminComponentName;

    public KioskModeManager(Activity act) {


        this.activity = act;

    }



    /**
     * enables KIOSK mode
     *
     * @param enabled true/false
     */
    public void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                Log.i("K","AÇ");
                if (devicePolicyManager.isLockTaskPermitted(activity.getPackageName())) {
                    activity.startLockTask();
                } else {
                    Log.i("Error","Kiosk mode Error");
                    //Logger.d("Kiosk Mode Error " + "Not permitted");
                }
            } else {
                Log.i("K","KAPAT");
                activity.stopLockTask();
            }
        } catch (Exception e) {
            Log.i("Error","Kiosk mode Error1");
            //Logger.e("Kiosk Mode Error" + e.getMessage());
        }
    }
}
