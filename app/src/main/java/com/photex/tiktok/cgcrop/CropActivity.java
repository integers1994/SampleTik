package com.photex.tiktok.cgcrop;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.photex.tiktok.R;

@SuppressWarnings("unused")
public class CropActivity extends Activity implements View.OnClickListener {
    public static Bitmap bitmapp;
    int width, height;
    RotateView rotateView;
    Matrix matrix = new Matrix();
    RectF rectF;
    float w, h;
    private float oldDegree;
    private boolean needApply = true;
    private SeekBar seekBar;
    private Button btnRatioFree, btnRatioOrigin, btnRatio1_1;
    private Button btnRatio3_4, btnRatio4_3, btnRatio9_16, btnRatio16_9, btn_flip_90;
    private float orignRatio = 1f;
    private float centerPrecent = 50f;
    private Button btnReset;
    private float ratio = 0f;
    private float backCoverRatio = 9f / 16f;
    private float profileRatio = 1f;

    TextView zoomInInfo;
    boolean isFromProfile = false;
    boolean isFromProfileBackCover = false;

    MenuItem menuItemDone;

    public static Bitmap decodeSampledBitmapFromResource(Context context,
                                                         String path, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = Math.min(heightRatio, widthRatio);
        }
        return inSampleSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_view);

        // InterstitialAdmob();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        height = metrics.heightPixels;
        width = metrics.widthPixels;

        Bitmap original = getPhoto(getIntent().getStringExtra("image_path"));

        if (getIntent() != null && getIntent().hasExtra("fromProfile")) {
            isFromProfile = true;
        } else if (getIntent() != null && getIntent().hasExtra("fromProfileBackCover")) {
            isFromProfileBackCover = true;
        }
        if (original == null) {
            Toast.makeText(CropActivity.this, "select another photo", Toast.LENGTH_SHORT).show();
            CropActivity.this.finish();
        }

        btnRatioFree = (Button) findViewById(R.id.btn_free);
        btnRatio1_1 = (Button) findViewById(R.id.btn_1_1);
        btnRatio3_4 = (Button) findViewById(R.id.btn_3_4);
        btnRatio4_3 = (Button) findViewById(R.id.btn_4_3);
        btnRatio9_16 = (Button) findViewById(R.id.btn_9_16);
        btnRatio16_9 = (Button) findViewById(R.id.btn_16_9);
        btnRatioOrigin = (Button) findViewById(R.id.btn_origin);
        btn_flip_90 = (Button) findViewById(R.id.btn_flip_90);
        zoomInInfo = (TextView) findViewById(R.id.zoomInInfo);

        btnRatioFree.setOnClickListener(this);
        btnRatioOrigin.setOnClickListener(this);
        btnRatio1_1.setOnClickListener(this);
        btnRatio3_4.setOnClickListener(this);
        btnRatio4_3.setOnClickListener(this);
        btnRatio16_9.setOnClickListener(this);
        btnRatio9_16.setOnClickListener(this);


        rotateView = (RotateView) findViewById(R.id.rotate);
        rotateView.setOriginal(original.getWidth(), original.getHeight());
        rotateView.setRectPadding(20);
        w = original.getWidth();
        h = original.getHeight();
        rectF = new RectF(0, 0, w, h);
        rotateView.setPhotoBounds(rectF);
        rotateView.setImageBitmap(original);

        rotateView.setImageMatrix(matrix);

        rotateView.setOperatedListenner(new RotateView.OperateListenner() {
            @Override
            public void hasOprated() {
                btnReset.setVisibility(View.VISIBLE);
            }
        });
        rotateView.setCropListenenr(new RotateView.CropListenner() {
            @Override
            public void onCropping() {
                seekBar.setVisibility(View.GONE);
            }

            @Override
            public void cropFinished() {
                seekBar.setVisibility(View.VISIBLE);
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setMax(100);
        seekBar.setProgress(50);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (needApply == true) {
                    float p = (float) i;
                    float degree = (p - centerPrecent) / centerPrecent
                            * (centerPrecent - 5);
                    rotateView.rotate(degree, matrix);
                    rotateView.scaleImage();

                } else {
                    needApply = true;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                rotateView.stopRaotae();
            }
        });
        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // btnReset.setVisibility(View.GONE);
                oldDegree = 0;
                needApply = false;
                if (isFromProfile || isFromProfileBackCover) {

                    if (isFromProfile) {
                        ratio = profileRatio;
                    } else if (isFromProfileBackCover) {
                        ratio = backCoverRatio;
                    }
                    rotateView.updateCropRatio(ratio);
                } else {
                    rotateView.setRectRatio(0f);
                }

                rotateView.reset();
                rotateView.updateCropBound();
                rotateView.calRotate();

                seekBar.setProgress(50);
            }
        });

        findViewById(R.id.btn_flip_moriior).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotateView.verticalFlip(matrix, oldDegree);
                        oldDegree = -oldDegree;
                    }
                });

        btn_flip_90.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotateView.trunningRotate();
                    }
                });

        findViewById(R.id.btn_invalide).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotateView.checkBound(null, null);
                    }
                });

        findViewById(R.id.btn_crop).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rotateView.cropImage();
                    }
                });
        findViewById(R.id.img_done).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (bitmapp != null && !bitmapp.isRecycled()) {
                            bitmapp.recycle();
                            bitmapp = null;
                        }
                        try {
                            bitmapp = rotateView.process();
                        } catch (Exception e) {
                            Toast.makeText(CropActivity.this,
                                    "Try another photo", Toast.LENGTH_SHORT)
                                    .show();
                        }
                        CropActivity.this.finish();
                    }
                });

        if (isFromProfileBackCover) {
            btn_flip_90.setVisibility(View.GONE);
        } else {
            btn_flip_90.setVisibility(View.VISIBLE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isFromProfile || isFromProfileBackCover) {

                    zoomInInfo.setVisibility(View.VISIBLE);
                    seekBar.setProgress(50);
                    if (isFromProfile) {
                        ratio = profileRatio;
                    } else if (isFromProfileBackCover) {
                        ratio = backCoverRatio;
                    }


                    rotateView.updateCropRatio(ratio);
                    menuItemDone.setEnabled(true);
                }
            }
        }, 1500);


    }

    Bitmap getPhoto(String path) {

        Bitmap photoBitmap = null;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bitmapOptions);
        int imageWidth = bitmapOptions.outWidth;
        int imageHeight = bitmapOptions.outHeight;
        float scale = 1.0f;
        if (imageWidth < imageHeight) {
            if (imageHeight > width * 1.0f) {
                scale = width * 1.0f / (imageHeight * 1.0f);
            }
        } else {
            if (imageWidth > width * 1.0f) {
                scale = width * 1.0f / (imageWidth * 1.0f);
            }

        }
        photoBitmap = decodeSampledBitmapFromResource(this, path,
                (int) (imageWidth * scale), (int) (imageHeight * scale));
        return photoBitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        return super.onTouchEvent(event);

    }

    @Override
    public void onClick(View v) {
        seekBar.setProgress(50);

        int id = v.getId();
        if (id == R.id.btn_origin) {
            if (isFromProfile || isFromProfileBackCover) {

                if (isFromProfile) {
                    ratio = profileRatio;
                } else if (isFromProfileBackCover) {
                    ratio = backCoverRatio;
                }
            } else {
                ratio = orignRatio;
            }
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_free) {
            ratio = 0f;
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_1_1) {
            ratio = 1f;
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_3_4) {
            ratio = 4f / 3f;
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_4_3) {
            ratio = 3f / 4f;
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_16_9) {
            ratio = 9f / 16f;
            rotateView.updateCropRatio(ratio);
        } else if (id == R.id.btn_9_16) {
            ratio = 16f / 9f;
            rotateView.updateCropRatio(ratio);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.donecropingbtn, menu);
        menuItemDone = menu.findItem(R.id.doncroplib);
        if (isFromProfile || isFromProfileBackCover) {
            menuItemDone.setEnabled(false);
        } else {
            menuItemDone.setEnabled(true);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
            case R.id.doncroplib:
                try {
                    if (bitmapp != null && !bitmapp.isRecycled()) {
                        // bitmapp.recycle();
                        bitmapp = null;
                        System.gc();
                    }
                    bitmapp = rotateView.process();
                } catch (Exception e) {
                    Toast.makeText(CropActivity.this, "Try another photo",
                            Toast.LENGTH_SHORT).show();
                }
                if (isFromProfile || isFromProfileBackCover) {
//                    Intent intent = new Intent(this, UserProfileActivity.class);
                    setResult(RESULT_OK);
                }
                CropActivity.this.finish();
                // if (mInterstitialAd.isLoaded()) {
                // mInterstitialAd.show();
                // }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isFromProfile || isFromProfileBackCover) {
//            Intent intent = new Intent(this, UserProfileActivity.class);
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }
}
