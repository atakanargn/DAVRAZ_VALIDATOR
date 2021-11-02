package com.telpo.davraz;

import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.util.Log;

import java.util.logging.Logger;

public class KioskModeManager {
    Activity activity;
    private DevicePolicyManager devicePolicyManager;        // Device policy manager for activate Device admin

    public KioskModeManager(Activity act) {
        activity = act;
        devicePolicyManager = (DevicePolicyManager) activity.getSystemService(DEVICE_POLICY_SERVICE);    // Initializing device policy manager
    }

    /**
     * enables KIOSK mode
     *
     * @param enabled true/false
     */
    public void enableKioskMode(boolean enabled) {
        try {
            if (enabled) {
                if (devicePolicyManager.isLockTaskPermitted(activity.getPackageName())) {
                    activity.startLockTask();
                } else {
                    Log.i("Error","Kiosk mode Error");
                    //Logger.d("Kiosk Mode Error " + "Not permitted");
                }
            } else {
                activity.stopLockTask();
            }
        } catch (Exception e) {
            Log.i("Error","Kiosk mode Error1");
            //Logger.e("Kiosk Mode Error" + e.getMessage());
        }
    }
}
