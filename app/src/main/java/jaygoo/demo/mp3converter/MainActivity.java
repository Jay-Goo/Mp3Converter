package jaygoo.demo.mp3converter;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.File;

import jaygoo.library.converter.Mp3Converter;


public class MainActivity extends AppCompatActivity {
    long fileSize;
    long bytes = 0;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.sample_text);
        TextView versionTv = (TextView) findViewById(R.id.version_text);
        versionTv.setText("Lame Version: " + Mp3Converter.getLameVersion());
        findViewById(R.id.convertBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConvert();
            }
        });

    }


    private void startConvert(){

        //please set your file
        final String wavPath = Environment.getExternalStorageDirectory().getPath()+ File.separator+"test.wav";
        final String mp3Path = Environment.getExternalStorageDirectory().getPath()+File.separator+"test.mp3";
        Mp3Converter.init(44100,1,0,44100,96,7);
        fileSize = new File(wavPath).length();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mp3Converter.convertMp3(wavPath,mp3Path);
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
            if (handler != null && progress != 100){
                handler.postDelayed(this, 1000);
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
}
