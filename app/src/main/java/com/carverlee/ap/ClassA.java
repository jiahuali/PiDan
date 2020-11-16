package com.carverlee.ap;

import android.util.Log;

import com.carverlee.pidan.annotation.TimeCheck;

@TimeCheck(tag = "transsion-ClassA")
public class ClassA {

    private static final String TAG = ClassA.class.getCanonicalName();

    public void testClassA() {
        Log.i(TAG, "testClassA: ");
    }
}
