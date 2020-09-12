package com.yt.learn.media.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/11
 * 描述：预览界面
 */
public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Camera mCamera;
    private Context context;


    public CameraTextureView(@NonNull Context context) {
        this(context,null);
    }

    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraTextureView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =  context;
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull final SurfaceTexture surface, int width, int height) {
        //开启相机，相机开启时要一定的时间，避免界面不能及时渲染，放到线程中执行
        ThreadHelper.getInstance().runOnHandlerThread(new Runnable() {
            @Override
            public void run() {
                openCamera();
                startPreview(surface);
            }
        });

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    /**
     * 打开相机
     */
    private void openCamera(){
        int number = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo= new Camera.CameraInfo();
        for (int i = 0;i< number;i++){
            Camera.getCameraInfo(i,cameraInfo);
            //打开后置摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                mCamera = Camera.open(i);
                CameraUtil.setCameraDisplayOrientation(context,i,mCamera);
            }
        }
    }

    /**
     * 开始预览
     */
    private void startPreview(SurfaceTexture texture){
        if (mCamera != null){
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {

                }
            });
            try {
                mCamera.setPreviewTexture(texture);
                mCamera.startPreview();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭相机
     */
    private void releaseCamera(){
        if (mCamera != null){
            try {
                mCamera.stopPreview();
                mCamera.setPreviewDisplay(null);
                mCamera.release();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
