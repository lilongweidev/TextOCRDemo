package com.llw.textocr;

import androidx.annotation.Nullable;
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

import java.io.File;

public class MainActivity extends AppCompatActivity {

    /**
     * 通用文字识别请求码
     */
    private static final int REQUEST_CODE_GENERAL_BASIC = 100;
    /**
     * 对应百度平台上的应用apiKey
     */
    private String apiKey = "gQm5vnWxGuz5khN4IZ16yriL";
    /**
     * 对应百度平台上的应用secretKey
     */
    private String secretKey = "c8t796hbq0DXdsngSsOou5FCK2fFckpn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                Log.d("result-->","成功！"+token);
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.d("result-->","失败！"+error.getMessage());

            }
        }, getApplicationContext(),  apiKey, secretKey);
    }

    /**
     * Toast提示
     * @param msg
     */
    private void showMsg(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取保存文件
     * @param context
     * @return
     */
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    /**
     * 通用文字识别
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
     * Activity回调
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
                            Log.d("result-->",result);
                        }
                    });
        }
    }
}
