package com.oasisfeng.condom;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * The original CondomProcessPackageManager doesn't support permission spoofing, we use an patched
 * one to replace it after setting up.
 */
public class CondomProcessPatch {
    private static final String TAG = CondomProcessPatch.class.getSimpleName();

    public static void patchPM(@NonNull Context context) throws Exception {
        Log.d(TAG, "Patching package manager, time " + System.currentTimeMillis());
        final Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
        final Field ActivityThread_sPackageManager = ActivityThread.getDeclaredField("sPackageManager");
        ActivityThread_sPackageManager.setAccessible(true);
        final Class<?> IPackageManager = Class.forName("android.content.pm.IPackageManager");

        final Object pm = ActivityThread_sPackageManager.get(null);
        InvocationHandler handler;
        if (Proxy.isProxyClass(pm.getClass()) && (handler = Proxy.getInvocationHandler(pm)) instanceof AdvancedCondomProcessPackageManager) {
            Log.w(TAG, "AdvancedCondomProcessPackageManager was already installed in this process, skipping");
        } else if ((handler = Proxy.getInvocationHandler(pm)) instanceof CondomProcess.CondomProcessPackageManager) {
            Log.w(TAG, "Original CondomProcessPackageManager was installed in this process, converting.");
            final Object condom_pm = Proxy.newProxyInstance(context.getClassLoader(), new Class[] { IPackageManager },
                    new AdvancedCondomProcessPackageManager(context, (CondomProcess.CondomProcessPackageManager) handler, pm));
            ActivityThread_sPackageManager.set(null, condom_pm);
        } else {
            // We don't create a new CondomCore.
            throw new IllegalStateException("This method should only be called after CondomProcess#install.");
        }
        Log.i(TAG, "Finish patching. time " + System.currentTimeMillis());
    }
}
