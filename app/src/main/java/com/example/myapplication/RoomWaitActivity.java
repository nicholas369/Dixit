package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class RoomWaitActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private TextView tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6;
    private TextView tv_notice;

    private Button btn_ready;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_wait);

        tv_p1 = findViewById(R.id.tv_p1);
        tv_p2 = findViewById(R.id.tv_p2);
        tv_p3 = findViewById(R.id.tv_p3);
        tv_p4 = findViewById(R.id.tv_p4);
        tv_p5 = findViewById(R.id.tv_p5);
        tv_p6 = findViewById(R.id.tv_p6);
        TextView[] tv_p_arr = new TextView[]{tv_p1, tv_p2, tv_p3, tv_p4, tv_p5, tv_p6};
        tv_notice = findViewById(R.id.tv_notice);
        btn_ready = findViewById(R.id.btn_ready);

        Intent intent = getIntent();
        String user_num = intent.getExtras().getString("user_num");
        String user_score = intent.getExtras().getString("user_score");
        String user_name = intent.getExtras().getString("user_name");
        try {
            JSONArray user_li = new JSONArray(intent.getExtras().getString("user_li"));

            for (int i=0; i<user_li.length(); i++) {
                JSONObject user = new JSONObject((user_li.get(i).toString()));

                tv_p_arr[i].setText(user.getString("user_name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        btn_ready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(InetAddress.getByName(host), port);
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            jsonRequest = new JSONObject();
                            jsonRequest.put("code", "REQUEST_READY");

                            //bufferedWriter.wr
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }
}