package com.example.opencvapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    TextView hint;
    Button Go_btn, Clear_btn;
    ImageView src_image, res_image;

    BitmapDrawable drawable;
    Bitmap  bitmap;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        hint = findViewById(R.id.Hint);
        Go_btn = findViewById(R.id.Go_button);
        Clear_btn = findViewById(R.id.Clear_button);
        src_image = findViewById(R.id.source_imageview);
        res_image = findViewById(R.id.response_imageview);

        //初始化python环境
        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
        Python python_cv = Python.getInstance();

        // Go_button Function
        Go_btn.setOnClickListener(view -> {
            // 获取源图片并转换为Bitmap对象
            drawable = (BitmapDrawable) src_image.getDrawable();
            bitmap = drawable.getBitmap();
            // 将Bitmap转换为byte[]对象
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // 调用Python方法处理图片
            PyObject cvObject = python_cv.getModule("opencv_python");
            byte[] bytes = cvObject.callAttr("opencv_process_image",byteArray).toJava(byte[].class);

            // 将处理后的图片显示到画面上
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            res_image.setImageBitmap(bmp);
            hint.setText("Image Transfer Complete");
        });

        // Clear_button Function
        Clear_btn.setOnClickListener(view -> {
            res_image.setImageBitmap(null);
            hint.setText("Clear Response");
        });

    }
}