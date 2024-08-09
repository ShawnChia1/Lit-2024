package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.example.myapplication.Reply;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toolbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private List<Message> messages;
    private MessageAdapter messageAdapter;
    private final String TAG = "MainActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar overflowMenu = findViewById(R.id.toolbar);
        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.getMenu().getItem(0).setTitle("Back");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getTitle().equals("Back")) {
                            onBackPressed();
                            return true;
                        } else if (item.getTitle().equals("Report")) {
                            Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

        messages = new ArrayList<>();
        messages.add(new Message("Hello Shawn, do you have any questions?", false));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(messages, this, recyclerView);
        recyclerView.setAdapter(messageAdapter);

        EditText editTextMessage = findViewById(R.id.editTextMessage);
        MaterialButton buttonSend = findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                String prompt = String.valueOf(editTextMessage.getText());
                editTextMessage.setText("");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getHttpCustomSearch(prompt);
                    }
                }).start();
                messageAdapter.addMessage(new Message(prompt, true));
            }
        });
    }

    private void getHttpCustomSearch(String prompt) {
        String cx = "d7a1ae5beb8ce4696";
        String apiKey = "AIzaSyD1Ne7Vwx7s9bBnyDvpTxliluKC10m-RdE";
        String fields = "items(title,link,snippet)";
        String url = "https://www.googleapis.com/customsearch/v1?" + "q=" + prompt + "&cx=" + cx + "&key=" + apiKey + "&fields=" + fields;
        Request request = new Request.Builder()
                .url(url)
                .build();
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                postHttp(null, responseBody, prompt);
            }
        });
    }

    private void postHttp(RecyclerView recyclerView, String responseBody, String prompt) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("model", "gpt-4");
        JsonArray msgs = new JsonArray();
        List<Message> messages1 = new ArrayList<>();
        messages1.add(new Message("Data is from reliable news agencies: " + responseBody + "Tell me if " + prompt + " is fake news and provide link from data to backup your conclusion", true));
        Log.d(TAG, "Size: " + messages1.size());
        for (Message message : messages1) {
            JsonObject msg = new JsonObject();
            if (message.isUser()) {
                msg.addProperty("role", "user");
            } else {
                msg.addProperty("role", "system");
            }
            msg.addProperty("content", message.getContent());
            msgs.add(msg);
        }
        jsonObject.add("messages", msgs);
        Log.d(TAG, "Request: " + jsonObject.toString());
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + "sk-proj-fttpzq1irm9DoId34VlWT3BlbkFJemA5tmofKqr0Qp3R9WKI")
                .build();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // Connection timeout
                .readTimeout(30, TimeUnit.SECONDS)    // Read timeout
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response: " + responseBody);
                    Reply res = new Gson().fromJson(responseBody, Reply.class);
                    String reply = res.getChoices().get(0).getMessage().getContent();
                    Log.d(TAG, "Reply: " + reply);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            messageAdapter.addMessage(new Message(reply, false));
                        }
                    });
                } else {
                    Log.d(TAG, "something went wrong");
                    Log.d(TAG, response.body().string());
                }
            }
        });
    }
}