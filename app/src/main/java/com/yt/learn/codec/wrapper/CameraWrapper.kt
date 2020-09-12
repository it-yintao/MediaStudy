package com.yt.learn.codec.wrapper

import android.graphics.SurfaceTexture
import android.hardware.Camera
import com.yt.learn.codec.entity.CodecContext

/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：相机操作类
 */
class CameraWrapper(private var context:CodecContext,
                    private var onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener){

    private var mCamera:Camera? = null
    private var mCameras = 0
    private var mCameraIndex: CameraIndex?=null


    /**
     * 打开摄像头
     */
    private fun openCamera(index: CameraIndex){

    }

    private fun openCamera(index: Int):Camera?{
        return null
    }

    /**
     * 开始预览
     */
    private fun startPreview(){

    }

    /**
     * 停止预览
     */
    private fun stopPreview(){

    }


    /**
     * 切换摄像头
     */
    fun switchCamera(index: CameraIndex){

    }

    /**
     * 摄像头选择：前置、后置
     */
    enum class CameraIndex{
        BACK,FRONT
    }




}