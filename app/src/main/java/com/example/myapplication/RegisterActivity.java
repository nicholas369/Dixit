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

public class RegisterActivity extends AppCompatActivity {

    final static private String host = "127.0.0.1";
    final static private int port = 8080;

    private JSONObject jsonRequest, jsonResponse;

    private EditText et_id, et_pw, et_name;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_name = findViewById(R.id.et_name);
        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = et_id.getText().toString();
                String user_pw = et_pw.getText().toString();
                String user_name = et_name.getText().toString();

                new Thread() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("before socket");
                            Socket socket = new Socket(InetAddress.getByName(host), port);
                            System.out.println("after socket");
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            jsonRequest = new JSONObject();
                            jsonRequest.put("code", "REQUEST_REGISTER");
                            jsonRequest.put("user_id", user_id);
                            jsonRequest.put("user_pw", user_pw);
                            jsonRequest.put("user_name", user_name);

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

                    if (code.equals("SUCCESS_REGISTER")) {
                        //String user_name = jsonResponse.getString("user_name");
                        String user_num = jsonResponse.getString("user_num");
                        String user_score = jsonResponse.getString("user_score");
                        JSONArray room_li = jsonResponse.getJSONArray("room_li");

                        Toast.makeText(getApplicationContext(), "Register Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, RoomListActivity.class);
                        intent.putExtra("user_num", user_num);
                        intent.putExtra("user_score", user_score);
                        intent.putExtra("user_name", user_name);
                        intent.putExtra("room_li", room_li.toString());

                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Register Failed", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}