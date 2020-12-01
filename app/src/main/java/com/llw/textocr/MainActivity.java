package com.llw.textocr;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.google.gson.Gson;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * 通用文字识别请求码
     */
    private static final int REQUEST_CODE_GENERAL_BASIC = 100;
    /**
     * 通用文字识别（高精度）请求码
     */
    private static final int REQUEST_CODE_ACCURATE_BASIC = 101;

    /**
     * 对应百度平台上的应用apiKey
     */
    private String apiKey = "gQm5vnWxGuz5khN4IZ16yriL";

    /**
     * 对应百度平台上的应用secretKey
     */
    private String secretKey = "c8t796hbq0DXdsngSsOou5FCK2fFckpn";

    /**
     * 弹窗
     */
    private AlertDialog.Builder mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //实例化，否则使用会报错 null object
        mDialog = new AlertDialog.Builder(this);

        initTextSDK();
    }

    /**
     * 用明文ak，sk初始化
     */
    private void initTextSDK() {
        OCR.getInstance(this).initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.d("result-->", "成功！" + token);
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.d("result-->", "失败！" + error.getMessage());

            }
        }, getApplicationContext(), apiKey, secretKey);
    }

    /**
     * Toast提示
     *
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取保存文件
     *
     * @param context
     * @return
     */
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    /**
     * 通用文字识别
     *
     * @param view
     */
    public void generalBasic(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        //传入文件保存的路径
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, getSaveFile(getApplication()).getAbsolutePath());
        //传入文件类型
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
        //跳转页面时传递请求码，返回时根据请求码判断获取识别的数据。
        startActivityForResult(intent, REQUEST_CODE_GENERAL_BASIC);
    }

    /**
     * 通用文字识别 （高精度版）
     *
     * @param view
     */
    public void highPrecision(View view) {
        Intent intent = new Intent(MainActivity.this, CameraActivity.class);
        intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH, getSaveFile(getApplication()).getAbsolutePath());
        intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_GENERAL);
        startActivityForResult(intent, REQUEST_CODE_ACCURATE_BASIC);
    }


    /**
     * Activity回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 识别成功回调，通用文字识别
        if (requestCode == REQUEST_CODE_GENERAL_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recGeneralBasic(this, getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            showMsg(result);
                            Log.d("result-->", result);
                        }
                    });
        }
        // 识别成功回调，通用文字识别（高精度版）
        if (requestCode == REQUEST_CODE_ACCURATE_BASIC && resultCode == Activity.RESULT_OK) {
            RecognizeService.recAccurateBasic(this, getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            //弹窗显示识别内容
                            showDialog(result);
                            Log.d("result-->", result);
                        }
                    });
        }
    }

    /**
     * 显示识别结果弹窗
     * @param result
     */
    private void showDialog(final String result) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //数据解析 Gson将JSON字符串转为实体Bean
                TextResult textResult = new Gson().fromJson(result, TextResult.class);
                if (textResult.getWords_result() == null && textResult.getWords_result().size() <= 0) {
                    return;
                }
                String text = "";
                //数据不为空并且大于0
                for(int i = 0;i<textResult.getWords_result().size();i++){
                    text += textResult.getWords_result().get(i).getWords()+"\n";
                }
                mDialog.setMessage(text)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }


}
