package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Text;

public class RoomActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private TextView tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6;
    private TextView tv_notice;

    private ImageView iv_card1, iv_card2, iv_card3, iv_card4, iv_card5, iv_card6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        tv_p1 = findViewById(R.id.tv_p1);
        tv_p2 = findViewById(R.id.tv_p2);
        tv_p3 = findViewById(R.id.tv_p3);
        tv_p4 = findViewById(R.id.tv_p4);
        tv_p5 = findViewById(R.id.tv_p5);
        tv_p6 = findViewById(R.id.tv_p6);
        tv_notice = findViewById(R.id.tv_notice);
        iv_card1 = findViewById(R.id.iv_card1);
        iv_card2 = findViewById(R.id.iv_card2);
        iv_card3 = findViewById(R.id.iv_card3);
        iv_card4 = findViewById(R.id.iv_card4);
        iv_card5 = findViewById(R.id.iv_card5);
        iv_card6 = findViewById(R.id.iv_card6);

    }
}