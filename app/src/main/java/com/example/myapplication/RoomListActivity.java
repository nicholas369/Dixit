package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class RoomListActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private ListView lv_room;
    private Button btn_create, btn_enter, btn_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        lv_room = findViewById(R.id.lv_room);
        btn_create = findViewById(R.id.btn_create);
        btn_enter = findViewById(R.id.btn_enter);
        btn_refresh = findViewById(R.id.btn_refresh);

        List<String> data = new ArrayList<>();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        lv_room.setAdapter(arrayAdapter);

        Intent intent = getIntent();
        String user_num = intent.getExtras().getString("user_num");
        String user_score = intent.getExtras().getString("user_score");
        String user_name = intent.getExtras().getString("user_name");
        try {
            JSONArray room_li = new JSONArray(intent.getExtras().getString("room_li"));

            for (int i=0; i<room_li.length(); i++) {
                JSONObject room = new JSONObject((room_li.get(i).toString()));
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(room.getString("room_name"));
                stringBuilder.append("[");
                stringBuilder.append(room.getString("room_member"));
                stringBuilder.append("]");
                data.add(i, stringBuilder.toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_refresh.setOnClickListener(new View.OnClickListener() {
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
                            jsonRequest.put("code", "REQUEST_ROOMLI");

                            bufferedWriter.write(jsonRequest.toString());
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }

                            jsonResponse = new JSONObject(stringBuilder.toString());
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();

                while (jsonResponse == null);

                String code = null;
                try {
                    code = jsonResponse.getString("code");

                    if (code.equals("SUCCESS_REFRESH")) {
                        JSONArray room_li = jsonResponse.getJSONArray("room_li");

                        for (int i=0; i<room_li.length(); i++) {
                            JSONObject room = new JSONObject((room_li.get(i).toString()));
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(room.getString("room_name"));
                            stringBuilder.append("[");
                            stringBuilder.append(room.getString("room_member"));
                            stringBuilder.append("]");
                            data.add(i, stringBuilder.toString());
                        }

                        Toast.makeText(getApplicationContext(), "Refresh Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Refresh Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create room
                Intent intent = new Intent(RoomListActivity.this, RoomCreateActivity.class);
                intent.putExtra("user_num", user_num);
                intent.putExtra("user_score", user_score);
                intent.putExtra("user_name", user_name);
                startActivity(intent);
            }
        });

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Enter room
                Intent intent = new Intent(RoomListActivity.this, RoomWaitActivity.class);
                startActivity(intent);
            }
        });
    }
}