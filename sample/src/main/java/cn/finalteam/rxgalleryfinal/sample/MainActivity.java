package cn.finalteam.rxgalleryfinal.sample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.yalantis.ucrop.model.AspectRatio;
import cn.finalteam.rxgalleryfinal.RxGalleryFinal;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.imageloader.ImageLoaderType;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultSubscriber;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageMultipleResultEvent;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.RxGalleryListener;
import cn.finalteam.rxgalleryfinal.ui.base.IMultiImageCheckedListener;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.MediaScanner;
import cn.finalteam.rxgalleryfinal.utils.ModelUtils;

/**
 * 示例
 * @author KARL-dujinyang
 */
public class MainActivity extends AppCompatActivity {

    RadioButton mRbRadioIMG, mRbMutiIMG, mRbOpenC,mRbRadioVD,mRbMutiVD,mRbCropZD,mRbCropZVD;
    Button mBtnOpenSetDir,mBtnOpenDefRadio,mBtnOpenDefMulti,mBtnOpenIMG,mBtnOpenVD,mBtnOpenCrop;
    boolean mFlagOpenCrop =false ; //是否拍照并裁剪

    //ID
    private void initView() {
        mBtnOpenSetDir = (Button) findViewById(R.id.btn_open_set_path);
        mBtnOpenIMG = (Button) findViewById(R.id.btn_open_img);
        mBtnOpenVD = (Button) findViewById(R.id.btn_open_vd);
        mBtnOpenDefRadio = (Button) findViewById(R.id.btn_open_def_radio);
        mBtnOpenDefMulti = (Button) findViewById(R.id.btn_open_def_multi);
        mBtnOpenCrop = (Button) findViewById(R.id.btn_open_crop);
        mRbRadioIMG = (RadioButton) findViewById(R.id.rb_radio_img);
        mRbMutiIMG = (RadioButton) findViewById(R.id.rb_muti_img);
        mRbRadioVD = (RadioButton) findViewById(R.id.rb_radio_vd);
        mRbMutiVD = (RadioButton) findViewById(R.id.rb_muti_vd);
        mRbOpenC = (RadioButton) findViewById(R.id.rb_openC);
        mRbCropZD = (RadioButton) findViewById(R.id.rb_radio_crop_z);
        mRbCropZVD = (RadioButton) findViewById(R.id.rb_radio_crop_vz);
    }

    //ImageLoaderConfiguration
    private void initImageLoader() {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        ImageLoader.getInstance().init(config.build());
    }

    //Fresco
    private void initFresco() {
        Fresco.initialize(this);
    }

    //*************************************************************************//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //手动打开日志
        ModelUtils.setDebugModel(true);
        initView();
        initImageLoader();
        initFresco();

        //自定义使用
        onClickZDListener();
        //调用图片选择器Api
        onClickSelImgListener();
        //调用视频选择器Api
        onClickSelVDListener();
        //调用裁剪
        onClickImgCropListener();
        //多选事件的回调
        getMultiListener();
    }

    //***********************************************************
    //**  以下为调用方法 onClick *
    //***********************************************************

    /**
     *  调用裁剪
     */
    private void onClickImgCropListener() {
        mBtnOpenCrop.setOnClickListener(view -> {
            if(mRbCropZD.isChecked()){
                //直接裁剪
                String inputImg = "";
                Toast.makeText(MainActivity.this, "没有图片演示，请选择‘拍照裁剪’功能", Toast.LENGTH_SHORT).show();
              //  RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), inputImg);//调用裁剪.RxGalleryFinalApi.getModelPath()为模拟的输出路径
            }else{
                //拍照并裁剪
                mFlagOpenCrop = true;
                //然后直接打开相机 - onActivityResult 回调里面处理裁剪
                RxGalleryFinalApi.openZKCamera(MainActivity.this);
            }
        });
    }

    /**
     *  调用视频选择器Api
     */
    private void onClickSelVDListener() {
        mBtnOpenVD.setOnClickListener(view -> {
            if (mRbRadioVD.isChecked()) {
                RxGalleryFinalApi.getInstance(this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                        .setVDRadioResultEvent(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                //回调还没
                            }
                        })
                        .open();



            } else if (mRbMutiVD.isChecked()) {
                //多选图片的方式
                //1.使用默认的参数
              /*  RxGalleryFinalApi.getInstance(this).setVDMultipleResultEvent(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        Logger.i("多选视频的回调");
                    }
                }).open();
*/
                //2.使用自定义的参数
                RxGalleryFinalApi.getInstance(this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_VIDEO, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setVDMultipleResultEvent(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
                            @Override
                            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                Logger.i("多选视频的回调");
                            }
                        }).open();;

                //3.直接打开
            /*    RxGalleryFinalApi.openMultiSelectVD(this, new RxBusResultSubscriber() {
                    @Override
                    protected void onEvent(Object o) throws Exception {
                        Logger.i("多选视频的回调");
                    }
                });*/
            }
        });
    }

    /**
     *  调用图片选择器Api
     */
    private void onClickSelImgListener() {
        mBtnOpenIMG.setOnClickListener(view -> {
            if (mRbRadioIMG.isChecked()) {
                //以下方式 -- 可选：
                //1.打开单选图片，默认参数
     /*             RxGalleryFinalApi.getInstance(this).setImageRadioResultEvent(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                        Logger.i("单选图片的回调");
                    }
                }).open();

                //2.设置自定义的参数
                RxGalleryFinalApi.getInstance(this).setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_RADIO)
                        .setImageRadioResultEvent(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Logger.i("单选图片的回调");
                            }
                        }).open();
*/
                //3. 快速打开单选图片,flag使用true不裁剪
                RxGalleryFinalApi.openRadioSelectImage(this, new RxBusResultSubscriber() {
                    @Override
                    protected void onEvent(Object o) throws Exception {

                    }
                },true);

            } else if (mRbMutiIMG.isChecked()) {
                //多选图片的方式
                //1.使用默认的参数
                RxGalleryFinalApi.getInstance(this).setImageMultipleResultEvent(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
                    @Override
                    protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                        Logger.i("多选图片的回调");
                    }
                }).open();
/*
                //2.使用自定义的参数
                RxGalleryFinalApi.getInstance(this)
                        .setType(RxGalleryFinalApi.SelectRXType.TYPE_IMAGE, RxGalleryFinalApi.SelectRXType.TYPE_SELECT_MULTI)
                        .setImageMultipleResultEvent(new RxBusResultSubscriber<ImageMultipleResultEvent>() {
                            @Override
                            protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                                Logger.i("多选图片的回调");
                            }
                        }).open();
                ;

                //3.直接打开
                RxGalleryFinalApi.openMultiSelectImage(this, new RxBusResultSubscriber() {
                    @Override
                    protected void onEvent(Object o) throws Exception {
                        Logger.i("多选图片的回调");
                    }
                });*/
            } else {
                mFlagOpenCrop = false;
                //直接打开相机
                RxGalleryFinalApi.openZKCamera(MainActivity.this);
            }
        });
    }


    /**
     * 如果不使用api定义好的，则自己定义使用
     * ImageLoaderType :自己选择使用
     */
    private void onClickZDListener() {

        mBtnOpenSetDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //图片会自动会存储到下面路径
                RxGalleryFinalApi.setImgSaveRxSDCard("dujinyang");
                RxGalleryFinalApi.setImgSaveRxCropSDCard("dujinyang/crop");//裁剪会自动生成路径；也可以手动设置裁剪的路径；
            }
        });

        mBtnOpenDefRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //单选图片
                RxGalleryFinal rx = RxGalleryFinal
                        .with(MainActivity.this)
                        .image()
                        .radio()
                        .imageLoader(ImageLoaderType.PICASSO)
                        .subscribe(new RxBusResultSubscriber<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Toast.makeText(getBaseContext(), imageRadioResultEvent.getResult().getOriginalPath(), Toast.LENGTH_SHORT).show();
                            }
                        });
                //自定义裁剪
                rx.cropAspectRatioOptions(0, new AspectRatio("3:3",30, 10)).crop()
                        .openGallery();

            }
        });

        mBtnOpenDefMulti.setOnClickListener(view -> {
            //多选图片
            RxGalleryFinal
                    .with(MainActivity.this)
                    .image()
                    .multiple()
                    .maxSize(8)
                    .imageLoader(ImageLoaderType.UNIVERSAL)
                    .subscribe(new RxBusResultSubscriber<ImageMultipleResultEvent>() {

                        @Override
                        protected void onEvent(ImageMultipleResultEvent imageMultipleResultEvent) throws Exception {
                            Toast.makeText(getBaseContext(), "已选择" + imageMultipleResultEvent.getResult().size() + "张图片", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            Toast.makeText(getBaseContext(), "OVER", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .openGallery();

        });
    }


    /**
     * 多选事件都会在这里执行
     */
    public void getMultiListener(){
        //得到多选的事件
        RxGalleryListener.getInstance().setMultiImageCheckedListener(new IMultiImageCheckedListener() {
            @Override
            public void selectedImg(Object t, boolean isChecked) {
                //这个主要点击或者按到就会触发，所以不建议在这里进行Toast
            }

            @Override
            public void selectedImgMax(Object t, boolean isChecked, int maxSize) {
                Toast.makeText(getBaseContext(), "你最多只能选择" + maxSize + "张图片", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i("onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode + " data:" + data);
            if (requestCode == RxGalleryFinalApi.TAKE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                Logger.i("拍照OK，图片路径:"+RxGalleryFinalApi.fileImagePath.getPath().toString());
                    //刷新相册数据库
                    RxGalleryFinalApi.openZKCameraForResult(MainActivity.this, new MediaScanner.ScanCallback() {
                        @Override
                        public void onScanCompleted(String[] strings) {
                            Logger.i(String.format("拍照成功,图片存储路径:%s", strings[0]));
                            if (mFlagOpenCrop) {
                                Logger.d("演示拍照后进行图片裁剪，根据实际开发需求可去掉上面的判断");
                                RxGalleryFinalApi.cropScannerForResult(MainActivity.this, RxGalleryFinalApi.getModelPath(), strings[0]);//调用裁剪.RxGalleryFinalApi.getModelPath()为默认的输出路径
                            }
                        }
                    });
            } else {
                Logger.i("失敗");
            }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //选择调用：裁剪图片的回调
        RxGalleryFinalApi.cropActivityForResult(this, new MediaScanner.ScanCallback() {
            @Override
            public void onScanCompleted(String[] images) {
                Logger.i(String.format("裁剪图片成功,图片裁剪后存储路径:%s", images[0]));
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


}
