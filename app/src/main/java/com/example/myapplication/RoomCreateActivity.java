package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class RoomCreateActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private EditText et_name;

    private RadioGroup rdgroup;
    private RadioButton rdbtn_3, rdbtn_4, rdbtn_5, rdbtn_6;

    private Button btn_create;

    private int room_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_create);

        et_name = findViewById(R.id.et_name);
        rdgroup = findViewById(R.id.rdgroup);
        rdbtn_3 = findViewById(R.id.rdbtn_3);
        rdbtn_4 = findViewById(R.id.rdbtn_4);
        rdbtn_5 = findViewById(R.id.rdbtn_5);
        rdbtn_6 = findViewById(R.id.rdbtn_6);
        btn_create = findViewById(R.id.btn_create);

        Intent intent = getIntent();
        String user_num = intent.getExtras().getString("user_num");
        String user_score = intent.getExtras().getString("user_score");
        String user_name = intent.getExtras().getString("user_name");

        rdgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rdbtn_3) {
                    room_user = 3;
                    Toast.makeText(getApplicationContext(), "max 3", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.rdbtn_4){
                    room_user = 4;
                    Toast.makeText(getApplicationContext(), "max 4", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.rdbtn_5) {
                    room_user = 5;
                    Toast.makeText(getApplicationContext(), "max 5", Toast.LENGTH_SHORT).show();
                } else if (checkedId == R.id.rdbtn_6) {
                    room_user = 6;
                    Toast.makeText(getApplicationContext(), "max 6", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String room_name = et_name.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(InetAddress.getByName(host), port);
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            jsonRequest = new JSONObject();
                            jsonRequest.put("code", "REQUEST_ROOM_CREATE");
                            jsonRequest.put("user_num", user_num);
                            jsonRequest.put("room_name", room_name);
                            jsonRequest.put("room_user", room_user);

                            bufferedWriter.write(jsonRequest.toString());
                            bufferedWriter.newLine();
                            bufferedWriter.flush();

                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line);
                            }

                            jsonResponse = new JSONObject(stringBuilder.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                while (jsonResponse == null);

                String code = null;
                try {
                    code = jsonResponse.getString("code");

                    if (code.equals("SUCCESS_ROOM_CREATE")) {
                        String room_no = jsonResponse.getString("room_no");
                        JSONArray user_li = jsonResponse.getJSONArray("user_li");

                        Toast.makeText(getApplicationContext(), "Room Creation Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RoomCreateActivity.this, RoomWaitActivity.class);
                        intent.putExtra("user_num", user_num);
                        intent.putExtra("user_score", user_score);
                        intent.putExtra("user_name", user_name);
                        intent.putExtra("user_li", user_li.toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Room Creation Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}