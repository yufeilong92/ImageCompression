package com.lawyee.imagecompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnYaSuo;
    private String path = "/storage/sdcard1/花/u=1193384153,695031601&fm=21&gp=0.jpg";
    private ImageView mIvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    /**
     * 计算图片的压缩放置
     *
     * @param options
     * @param reqwith
     * @param reqhight
     * @return
     */
    public static int CalculatelSampleSize(BitmapFactory.Options options, int reqwith, int reqhight) {
        final int height = options.outHeight;
        final int with = options.outWidth;
        int inSampleSize = 1;
        if (height > reqhight || with > reqwith) {
            final int heightRatio = Math.round((float) height / (float) reqhight);
            final int widthRatio = Math.round((float) with / (float) reqwith);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        //calculae inSampleSize
        options.inSampleSize = CalculatelSampleSize(options, 480, 800);
        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //bitmap 转换成string
    public static String bitampToString(String filePath) {
        Bitmap bitmap = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 30, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b, Base64.DEFAULT);

    }


    private void initView() {
        mBtnYaSuo = (Button) findViewById(R.id.btn_YaSuo);

        mBtnYaSuo.setOnClickListener(this);
        mIvShow = (ImageView) findViewById(R.id.iv_show);
        mIvShow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_YaSuo:
                String s = bitampToString(path);
                Toast.makeText(MainActivity.this, "" + s, Toast.LENGTH_SHORT).show();
                Log.d("=====", "onClick: " + s);
                Bitmap smallBitmap = getSmallBitmap(path);
                mIvShow.setImageBitmap(smallBitmap);
                int scrollBarSize = mIvShow.getScrollBarSize();
                Log.d("====", "onClick: " + scrollBarSize);
                saveMyBitmap("测试图片",smallBitmap);
                break;
        }
    }

    public void saveMyBitmap(String bitName, Bitmap mBitmap) {
//获取内置存储下的缓存目录，可以用来保存一些缓存文件如图片，当内置存储的空间不足时将系统自动被清除
        File f = new File(this.getCacheDir() + bitName + ".png");
        Log.d("==", "压缩图片的路径"+this.getCacheDir() + bitName + ".png");
//        File f = new File("/sdcard/" + bitName + ".png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


/*    *//*
    压缩图片，处理某些手机拍照角度旋转的问题
    *//*
    public static String compressImage(Context context, String filePath, String fileName, int q) throws FileNotFoundException {

        Bitmap bm = getSmallBitmap(filePath);

        int degree = readPictureDegree(filePath);

        if(degree!=0){//旋转照片角度
            bm=rotateBitmap(bm,degree);
        }

        File imageDir = SDCardUtils.getImageDir(context);

        File outputFile=new File(imageDir,fileName);

        FileOutputStream out = new FileOutputStream(outputFile);

        bm.compress(Bitmap.CompressFormat.JPEG, q, out);

        return outputFile.getPath();
    }*/
  //  判断照片角度

/*
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }*/
   // 旋转照片
    public static Bitmap rotateBitmap(Bitmap bitmap,int degress) {
        if (bitmap != null) {
            Matrix m = new Matrix();
            m.postRotate(degress);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
            return bitmap;
        }
        return bitmap;
    }
}
