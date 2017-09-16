package com.czg.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.czg.headtoast.HeadToast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private int i = 0;

    public void onClick(View v) {
        String string="";
        for (int j = 0; j < 20; j++) {
            string+=i;
            if(j%5==0) {
                string+="\n";
            }
        }


        HeadToast.makeText(this, string, HeadToast.LENGTH_LONG).show();

        i++;
    }
}
