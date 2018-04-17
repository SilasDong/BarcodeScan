package com.newequator.barcodescan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.device.ScanManager;
import android.device.scanner.configuration.Triggering;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView Wv;
    final Handler myHandler = new Handler();

    private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action

    //private EditText showScanResult;
    //private Button btn;
    //private Button mScan;
    //private Button mClose;

    private Vibrator mVibrator;
    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            //showScanResult.setText("");
            mVibrator.vibrate(100);

            byte[] barcode = intent.getByteArrayExtra("barocode");
            //byte[] barcode = intent.getByteArrayExtra("barcode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            android.util.Log.i("debug", "----codetype--" + temp);
            barcodeStr = new String(barcode, 0, barocodelen);

            //showScanResult.setText(barcodeStr);
            javaCallJS(barcodeStr);

        }

    };

    //@Override
    //protected void onCreate(Bundle savedInstanceState) {
    //    // TODO Auto-generated method stub
    //    super.onCreate(savedInstanceState);
    //    Window window = getWindow();
    //    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    //    setContentView(R.layout.activity_main);
    //    mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    //    setupView();
    //
    //}
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Wv = (WebView)findViewById(R.id.webView1);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //Window window = getWindow();
        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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


    private void initScan() {
        // TODO Auto-generated method stub
        mScanManager = new ScanManager();
        mScanManager.openScanner();

        mScanManager.switchOutputMode(0);
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
    }

    private void setupView() {
        // TODO Auto-generated method stub
        //showScanResult = (EditText) findViewById(R.id.scan_result);
        //btn = (Button) findViewById(R.id.manager);
        //btn.setOnClickListener(new OnClickListener() {
        //
        //    @Override
        //    public void onClick(View arg0) {
        //        // TODO Auto-generated method stub
        //        if (mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
        //            mScanManager.setTriggerMode(Triggering.CONTINUOUS);
        //    }
        //});

        //mScan = (Button) findViewById(R.id.scan);
        //mScan.setOnClickListener(new OnClickListener() {
        //
        //    @Override
        //    public void onClick(View arg0) {
        //        // TODO Auto-generated method stub
        //        //if(type == 3)
        //        mScanManager.stopDecode();
        //        isScaning = true;
        //        try {
        //            Thread.sleep(100);
        //        } catch (InterruptedException e) {
        //            // TODO Auto-generated catch block
        //            e.printStackTrace();
        //        }
        //        mScanManager.startDecode();
        //    }
        //});

        //mClose = (Button) findViewById(R.id.close);
        //mClose.setOnClickListener(new OnClickListener() {
        //
        //    @Override
        //    public void onClick(View arg0) {
        //        // TODO Auto-generated method stub
        //        // if(isScaning) {
        //        //  isScaning = false;
        //        mScanManager.stopDecode();
        //        //}
        //    }
        //});

        //btn.setVisibility(View.GONE);

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        //if (mScanManager != null) {
        //    mScanManager.stopDecode();
        //    isScaning = false;
        //    unregisterReceiver(mScanReceiver);
        //}
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initScan();
        //showScanResult.setText("");
        IntentFilter filter = new IntentFilter();
        filter.addAction(SCAN_ACTION);
        registerReceiver(mScanReceiver, filter);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }


    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        public void start(){
            try{
                mScanManager.stopDecode();
                isScaning = true;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                mScanManager.startDecode();
                //Toast.makeText(mContext, "开启扫描", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(mContext, "开启扫描失败", Toast.LENGTH_SHORT).show();
            }
        }

        public void close(){
            try{
                mScanManager.stopDecode();
                //Toast.makeText(mContext, "停止扫描", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(mContext, "停止扫描失败", Toast.LENGTH_SHORT).show();
            }
        }

        public void scanner(){
            try{
                if(mScanManager.getTriggerMode() != Triggering.CONTINUOUS)
                    mScanManager.setTriggerMode(Triggering.CONTINUOUS);
                //Toast.makeText(mContext, "连续模式", Toast.LENGTH_SHORT).show();
            }catch (Exception ex){
                Toast.makeText(mContext, "开启连续模式失败", Toast.LENGTH_SHORT).show();
            }
        }

        public void test(){
            javaCallJS("test");
        }
    }

    public void javaCallJS(final String text){
        Wv.post(new Runnable() {
            @Override
            public void run() {
                Wv.loadUrl("javascript:callFromJava('"+text+"')");
            }
        });
    }

}
