package com.ghts.player.inter;

import android.os.Handler;

import java.lang.ref.WeakReference;

/**
 * Created by lijingjing on 17-7-28.
 */
public abstract class WeakHandler<T> extends Handler {
    private WeakReference<T> mOwner;

    public WeakHandler(T owner) {
        mOwner = new WeakReference<T>(owner);
    }

    public T getOwner() {
        return mOwner.get();
    }
}