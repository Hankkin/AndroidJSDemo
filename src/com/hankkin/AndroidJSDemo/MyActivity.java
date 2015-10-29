package com.hankkin.AndroidJSDemo;

import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.webkit.*;
import org.json.JSONException;
import org.json.JSONObject;

public class MyActivity extends Activity {

    protected static final String LOGTAG = "MainActivity";

    private WebView webView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                //TODO:执行相关操作
                returnToJs(true, (JSONObject) msg.obj);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        webView = (WebView) findViewById(R.id.my_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(this, "needserver2");
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                Log.i(LOGTAG, "shouldInterceptRequest url=" + url + ";threadInfo" + Thread.currentThread());
                WebResourceResponse response = null;
                if (url.contains("logo")) {
                    try {
                        InputStream localCopy = getAssets().open("droidyue.png");
                        response = new WebResourceResponse("image/png", "UTF-8", localCopy);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return response;
            }

            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.e(">>>>>>>",request.toString());
                return super.shouldInterceptRequest(view, request);
            }
        });
        setContentView(webView);
        webView.loadUrl("http://baetest.tjeasyshare.com/");
    }
    @JavascriptInterface
    public void send_comment(final String fun_name, final String json) {

        //根据fun_name处理不同业务
        new Thread(new Runnable() {
            @Override
            public void run() {
                //注意此处必须是异步处理
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String title = jsonObject.optString("title");
                String message = jsonObject.optString("message");
                //TODO:根据message执行相关操作
                Message msg = new Message();
                msg.what = 0;
                msg.obj = jsonObject;
            }
        }).start();


    }

    /**
     * 回掉JS方法将处理信息返回给JS
     * @param isSuccess
     * @param json
     */
    public void returnToJs(final boolean isSuccess, final JSONObject json){
        Log.d("----", "javascript:app_result('" + isSuccess + "','" + json.toString() + "')");
        String data = "javascript:app_result('" + isSuccess + "','" + json.toString() + "')";
        webView.loadUrl(data);
    }

}
