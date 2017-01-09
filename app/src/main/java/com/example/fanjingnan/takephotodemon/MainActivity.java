package com.example.fanjingnan.takephotodemon;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PHOTO = 0;
    private static final int CORP_PHOTO = 1;
    private static final int Album_PHOTO = 2;
    private static final int SCALE = 5;//照片缩小比例
    @BindView(R.id.btn_person_album)
    Button btnPersonAlbum;
    private Uri imageUri;
    @BindView(R.id.btn_person_photo)
    Button btnPersonPhoto;
    @BindView(R.id.btn_person_image)
    ImageView btnPersonImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }


    @OnClick(R.id.btn_person_photo)
    public void onClick() {
        //图片命名为output_image.jpg ， 并将它存放在手机SD卡的根目录下
        File outputImage = new File(Environment.getExternalStorageDirectory(), "image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);// Uri 对象标识着 output_image.jpg 这张图片的唯一地址
        //使用的是一个隐式Intent，系统会找出能够响应这个 Intent的活动去启动，这样照相机程序就会被打开，拍下的照片将会输出到 output_image.jpg中
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    @OnClick(R.id.btn_person_album)
    public void onAlbumClick() {
        File outputImage  = new File(Environment.getExternalStorageDirectory(),"image123.jpg");
        try {
            if(outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CORP_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CORP_PHOTO);
                }
                break;
            case CORP_PHOTO:
                ContentResolver resolver = getContentResolver();
                //照片的原始资源地址
                Uri originalUri = data.getData();
                try {
                    //使用ContentProvider通过URI获取原始图片
                    Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                    if (photo != null) {
                        //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                        //释放原始图片占用的内存，防止out of memory异常发生
                        photo.recycle();

                        btnPersonImage.setImageBitmap(smallBitmap);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case Album_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CORP_PHOTO);
                }
                break;
            default:
                break;
        }
    }



}
