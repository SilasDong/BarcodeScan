package com.newequator.barcodescan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import com.tencent.smtt.export.external.interfaces.JsResult ;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class MainActivity extends Activity {

    private WebView Wv;
    private BroadcastReceiver  mReceiver;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWebView();
        Intent intent = new Intent ("ACTION_BAR_SCANCFG");
        intent.putExtra("EXTRA_SCAN_MODE", 3);
        intent.putExtra("EXTRA_TRIG_MODE", 0);
        getApplicationContext().sendBroadcast(intent);
        mReceiver= new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                final String scanResult_1 = intent.getStringExtra("SCAN_BARCODE1");
                final String scanStatus = intent.getStringExtra("SCAN_STATE");
                if ("ok".equals(scanStatus)) {
                    javaCallJS(scanResult_1);
                } else {

                }
            }
        };
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView(){
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        Wv = (WebView)findViewById(R.id.webView1);
        final JavaScriptInterface myJavaScriptInterface
                = new JavaScriptInterface(this);

        Wv.getSettings().setLightTouchEnabled(true);
        Wv.getSettings().setJavaScriptEnabled(true);
        //设置适应Html5的一些方法
        Wv.getSettings().setDomStorageEnabled(true);
        //设置是否支持缩放，我这里为false，默认为true。
        Wv.getSettings().setSupportZoom(false);

        //设置是否显示缩放工具，默认为false
        Wv.getSettings().setBuiltInZoomControls(false);
        //设置自适应屏幕
        Wv.getSettings().setUseWideViewPort(true);
        // 缩放至屏幕的大小
        Wv.getSettings().setLoadWithOverviewMode(true);
        Wv.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
        Wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                String scheme = Uri.parse(url).getScheme();
                if("http".equals(scheme)||"https".equals(scheme)){
                    return false;//return false 可以正常加载网页，并可过滤掉一些不必要的跳转页面，使后退功能正常
                }else{
                    try{
                        Intent intent=new Intent();
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            }
        });
        Wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            public void onProgressChanged(WebView view, int progress) {
                if (progress >= 100) {
                    findViewById(R.id.img).setVisibility(View.GONE);
                    findViewById(R.id.webView1).setVisibility(View.VISIBLE);
                }
            }
        });
        Wv.loadUrl("http://driver.signedexpress.com");
    }

    private void registerReceiver()
    {
        IntentFilter mFilter= new IntentFilter("nlscan.action.SCANNER_RESULT");
        registerReceiver(mReceiver, mFilter);
    }

    private void unRegisterReceiver()
    {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    public void javaCallJS(final String text){
        Wv.post(new Runnable() {
            @Override
            public void run() {
                Wv.loadUrl("javascript:callFromJava('"+text+"')");
            }
        });
    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void start(){
            // 广播模式，开始连续模式
            //Intent intent = new Intent ("ACTION_BAR_SCANCFG");
            //intent.putExtra("EXTRA_SCAN_MODE", 3);
            //intent.putExtra("EXTRA_TRIG_MODE", 1);
            //getApplicationContext().sendBroadcast(intent);
        }

        @JavascriptInterface
        public void close(){
            // 广播模式，关闭连续模式

            Intent intent = new Intent ("ACTION_BAR_SCANCFG");
            intent.putExtra("EXTRA_SCAN_MODE", 3);
            intent.putExtra("EXTRA_TRIG_MODE", 0);
            getApplicationContext().sendBroadcast(intent);
        }

        @JavascriptInterface
        public void scanner(){
            // 广播模式，开始连续模式
            Intent intent = new Intent ("ACTION_BAR_SCANCFG");
            intent.putExtra("EXTRA_SCAN_MODE", 3);
            intent.putExtra("EXTRA_TRIG_MODE", 1);
            getApplicationContext().sendBroadcast(intent);
        }

        @JavascriptInterface
        public void test(){
            javaCallJS("test");
        }
    }



}
