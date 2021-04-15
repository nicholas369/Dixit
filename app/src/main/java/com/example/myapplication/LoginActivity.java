package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private EditText et_id, et_pw;
    private Button btn_login, btn_register, btn_guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        btn_guest = findViewById(R.id.btn_guest);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = et_id.getText().toString();
                String user_pw = et_pw.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket(InetAddress.getByName(host), port);
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            jsonRequest = new JSONObject();
                            jsonRequest.put("code", "REQUEST_LOGIN");
                            jsonRequest.put("user_id", user_id);
                            jsonRequest.put("user_pw", user_pw);

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

                    if (code.equals("SUCCESS_LOGIN")) {
                        String user_num = jsonResponse.getString("user_num");
                        String user_score = jsonResponse.getString("user_score");
                        String user_name = jsonResponse.getString("user_name");
                        JSONArray room_li = jsonResponse.getJSONArray("room_li");

                        Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, RoomListActivity.class);
                        intent.putExtra("user_num", user_num);
                        intent.putExtra("user_score", user_score);
                        intent.putExtra("user_name", user_name);
                        intent.putExtra("room_li", room_li.toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}