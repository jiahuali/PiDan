package com.carverlee.ap;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.carverlee.pidan.annotation.CheckScope;
import com.carverlee.pidan.annotation.TimeCheck;

@TimeCheck(tag = "transsion",scope = CheckScope.PACKAGE)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test1();
        test2();
    }

    @TimeCheck(tag = "transsion2")
    void test1() {
        new ClassA().testClassA();
    }

    void test2() {

    }
}