package com.oasisfeng.condom;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.lang.reflect.Method;

public class AdvancedCondomProcessPackageManager extends CondomProcess.CondomProcessPackageManager {
    private static final String TAG = "AdvPM";

    CondomCore mCondom;
    private PackageManager mPm;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String method_name = method.getName();
        Log.d(TAG, "invoke " + method_name);
        CondomPackageManager origAdvPM = new CondomPackageManager(mCondom, mPm, "AdvCondomProcessPM_Orig");
        switch (method_name) {
            case "getPackageInfo":
                Log.d(TAG, "Patching package info");
                return origAdvPM.getPackageInfo(args[0].toString(), Integer.parseInt(args[1].toString()));
            case "getApplicationInfo":
                Log.d(TAG, "Patching application info");
                return origAdvPM.getApplicationInfo(args[0].toString(), Integer.parseInt(args[1].toString()));
        }
        return super.invoke(proxy, method, args);
    }

    AdvancedCondomProcessPackageManager(Context context, CondomCore condomCore, Object pm) {
        super(condomCore, pm);
        mCondom = condomCore;
        // We won't use argument pm because it's the binder interface, but CondomPackageManager
        // needs a wrapped PackageManager.
        mPm = context.getPackageManager();
    }

    AdvancedCondomProcessPackageManager(Context context, CondomProcess.CondomProcessPackageManager original,
                                        Object pm) {
        this(context, original.mCondom, pm);
    }
}
