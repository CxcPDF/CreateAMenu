package com.mymenu.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {


    private MainUI mainUI;
    private LeftMenu leftMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainUI=new MainUI(this);
        setContentView(mainUI);
        leftMenu=new LeftMenu();
//        getSupportFragmentManager().beginTransaction().add(MainUI.LEFT_ID,leftMenu).commit();
        getSupportFragmentManager().beginTransaction().add(MainUI.LEFT_ID,leftMenu).commit();
    }
}
