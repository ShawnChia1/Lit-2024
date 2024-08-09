package com.example.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BrowserActivity extends AppCompatActivity {

    private final String TAG = "BrowserActivity";
    private WebView webView;
    private String url;
    private EditText editText;
    private boolean proceed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        ImageView overflowMenu = findViewById(R.id.overflowMenu);
        overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(BrowserActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
                popupMenu.getMenu().getItem(0).setTitle("Misinformation Tool");
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                       if (item.getTitle().equals("Misinformation Tool")) {
                           Intent intent = new Intent(BrowserActivity.this, MainActivity.class);
                           startActivity(intent);
                           return true;
                       } else if (item.getTitle().equals("Report")) {
                           Intent intent = new Intent(BrowserActivity.this, ReportActivity.class);
                           startActivity(intent);
                           return true;
                       }
                       return false;
                    }
                });
                popupMenu.show();
            }
        });

        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        editText = findViewById(R.id.editText);
        MaterialButton goButton = findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = editText.getText().toString();
                url = input;
                webView.loadUrl(url);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    private class WebAppInterface {
        @JavascriptInterface
        public void onUrlChange(String url) {
            Log.d("WebViewClient", "JavaScript URL change detected: " + url);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    editText.setText(url);
                    BrowserActivity.this.url = url;
                    if (url.contains("cursedcomments") && !proceed) {
                        webView.stopLoading();
                        showAlertDialog();
                    } else {
                        webView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                extractContentByTagName("p");
                            }
                        }, 3000);
                    }
                    if (proceed) {
                        proceed = false;
                    }
                }
            });
        }

        @JavascriptInterface
        public void onContentLoaded() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Content is fully loaded, now you can extract it
                    // extractContentByTagName("p");
                }
            });
        }
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "page started: " + url);
            super.onPageStarted(view, url, favicon);
            editText.setText(url);
            BrowserActivity.this.url = url;
            if (url.contains("cursedcomments") && !proceed) {
                webView.stopLoading();
                showAlertDialog();
            }
            if (proceed) {
                proceed = false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "page finished: " + url);
            super.onPageFinished(view, url);

            webView.evaluateJavascript(
                    "javascript:(function() {" +
                            "   function notifyContentLoaded() {" +
                            "       if (window.Android && typeof window.Android.onContentLoaded === 'function') {" +
                            "           window.Android.onContentLoaded();" +
                            "       }" +
                            "   }" +
                            "   window.addEventListener('popstate', function(event) {" +
                            "       Android.onUrlChange(window.location.href);" +
                            "   });" +
                            "   var oldPushState = history.pushState;" +
                            "   history.pushState = function() {" +
                            "       oldPushState.apply(history, arguments);" +
                            "       Android.onUrlChange(window.location.href);" +
                            "   };" +
                            "   var oldReplaceState = history.replaceState;" +
                            "   history.replaceState = function() {" +
                            "       oldReplaceState.apply(history, arguments);" +
                            "       Android.onUrlChange(window.location.href);" +
                            "   };" +
                            "   var observer = new MutationObserver(function(mutations) {" +
                            "       mutations.forEach(function(mutation) {" +
                            "           notifyContentLoaded();" +
                            "       });" +
                            "   });" +
                            "   observer.observe(document.body, { childList: true, subtree: true });" +
                            "})();",
                    null);

            extractContentByTagName("p");
        }
    }

    private void extractContentByTagName(String tagName) {
        // Inject JavaScript to extract content by tag name
        webView.evaluateJavascript(
                "(function() { " +
                        "    var elements = document.getElementsByTagName('" + tagName + "'); " +
                        "    var content = ''; " +
                        "    for (var i = 0; i < elements.length; i++) { " +
                        "        content += elements[i].innerText + '\\n'; " +
                        "    } " +
                        "    return content; " +
                        "})();",
                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // Handle the extracted content here
                        Log.d(TAG, "Extracted Content: " + value);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JsonObject jsonObject = new JsonObject();
                                jsonObject.addProperty("input", value);
                                String jsonString = new Gson().toJson(jsonObject);
                                RequestBody requestBody = RequestBody.create(jsonString, MediaType.parse("application/JSON"));
                                Request request = new Request.Builder()
                                        .url("https://api.openai.com/v1/moderations")
                                        .addHeader("Authorization", "Bearer sk-proj-fttpzq1irm9DoId34VlWT3BlbkFJemA5tmofKqr0Qp3R9WKI")
                                        .post(requestBody)
                                        .build();
                                OkHttpClient client = new OkHttpClient();
                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e(TAG, "", e);
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            String responseBody = response.body().string();
                                            Log.d(TAG, responseBody);
                                            GptResponse gptResponse = new Gson().fromJson(responseBody, GptResponse.class);
                                            ArrayList<GptResponse.Result> results = gptResponse.getResults();
                                            GptResponse.Result result = results.get(0);
                                            if (result.isFlagged()) {
                                                String[] r = checkForCategory(result);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(BrowserActivity.this, "This website has a " + r[1] + "% chance of containing " + r[0] + " content.", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                            }
                                        } else {
                                            Log.d(TAG, response.body().string());
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                }
        );
    }

    private String[] checkForCategory(GptResponse.Result result) {
        GptResponse.Categories category = result.getCategories();
        GptResponse.Category_scores score = result.getCategoryScores();
        String[] r = new String[2];
        if (category.isSexual()) {
            r[0] = "sexual";
            r[1] = ("" + score.getSexual()).substring(2, 4);
        }
        if (category.isHate()) {
            r[0] = "hate";
            r[1] = ("" + score.getHate()).substring(2, 4);
        }
        if (category.isHarassment()) {
            r[0] = "harassment";
            r[1] = ("" + score.getHarassment()).substring(2, 4);
        }
        if (category.isSelf_harm()) {
            r[0] = "self harm";
            r[1] = ("" + score.getSelf_harm()).substring(2, 4);
        }
        if (category.isSexual_minors()) {
            r[0] = "underage sex";
            r[1] = ("" + score.getSexual_minors()).substring(2, 4);
        }
        if (category.isHate_threatening()) {
            r[0] = "threatening speech";
            r[1] = ("" + score.getHate_threatening()).substring(2, 4);
        }
        if (category.isViolence_graphic()) {
            r[0] = "graphic violence";
            r[1] = ("" + score.getViolence_graphic()).substring(2, 4);
        }
        if (category.isSelf_harm_intent()) {
            r[0] = "self harm intent";
            r[1] = ("" + score.getSelf_harm_intent()).substring(2, 4);
        }
        if (category.isSelf_harm_instructions()) {
            r[0] = "self harm instructions";
            r[1] = ("" + score.getSelf_harm_instructions()).substring(2, 4);
        }
        if (category.isHarassment_threatening()) {
            r[0] = "threatening and harassment";
            r[1] = ("" + score.getHarassment_threatening()).substring(2, 4);
        }
        if (category.isViolence()) {
            r[0] = "violence";
            r[1] = ("" + score.getViolence()).substring(2, 4);
        }
        return r;
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning!");
        builder.setMessage("This website was flagged for racism");
        builder.setPositiveButton("Go back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (webView.canGoBack()) {
                    webView.goBack();
                }
            }
        });
        builder.setNegativeButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                proceed = true;
                webView.loadUrl(url);
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

