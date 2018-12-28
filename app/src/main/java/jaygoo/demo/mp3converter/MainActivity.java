package jaygoo.demo.mp3converter;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import jaygoo.library.converter.Mp3Converter;


public class MainActivity extends AppCompatActivity {
    long fileSize;
    long bytes = 0;
    TextView tv;
    final String aiffPath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"test.aiff";
    final String wavPath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"test.wav";
    final String mp3Path = Environment.getExternalStorageDirectory().getPath()+File.separator+"test.mp3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted(new Action<List<String>>() {
                @Override
                public void onAction(List<String> data) {
                    init();
                }
            })
            .start();


    }

    private void init() {

        putAssetsToSDCard("test.aiff", aiffPath);
        putAssetsToSDCard("test.wav", wavPath);

        tv = (TextView) findViewById(R.id.sample_text);
        TextView versionTv = (TextView) findViewById(R.id.version_text);
        versionTv.setText("Lame Version: " + Mp3Converter.getLameVersion());
        findViewById(R.id.aiffConvertBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConvert(aiffPath);
            }
        });

        findViewById(R.id.wavConvertBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConvert(wavPath);
            }
        });
    }


    private void startConvert(final String sourcePath){

        //please set your file
        Mp3Converter.init(44100,1,0,44100,96,7);
        fileSize = new File(sourcePath).length();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mp3Converter.convertMp3(sourcePath,mp3Path);
            }
        }).start();

        handler.postDelayed(runnable, 500);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            bytes = Mp3Converter.getConvertBytes();
            float progress = (100f * bytes / fileSize);
            if (bytes == -1){
                progress = 100;
            }
            tv.setText("convert progress: " +  progress);
            if (handler != null && progress < 100){
                handler.postDelayed(this, 1000);
            }else {
                tv.setText(mp3Path);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    public void putAssetsToSDCard(String assetsPath,
        String sdCardPath) {
        try {
            String mString[] = getAssets().list(assetsPath);
            if (mString.length == 0) { // 说明assetsPath为空,或者assetsPath是一个文件
                InputStream mIs = getAssets().open(assetsPath); // 读取流
                byte[] mByte = new byte[1024];
                int bt = 0;
                File file = new File(sdCardPath);
                if (!file.exists()) {
                    file.createNewFile(); // 创建文件
                } else {
                    return;//已经存在直接退出
                }
                FileOutputStream fos = new FileOutputStream(file); // 写入流
                while ((bt = mIs.read(mByte)) != -1) { // assets为文件,从文件中读取流
                    fos.write(mByte, 0, bt);// 写入流到文件中
                }
                fos.flush();// 刷新缓冲区
                mIs.close();// 关闭读取流
                fos.close();// 关闭写入流

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
