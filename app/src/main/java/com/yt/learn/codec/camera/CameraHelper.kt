package com.yt.learn.codec.camera

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.Camera

/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：相机参数设置,这个类目前针对Camera1 后面再扩张Camera2
 */
object CameraHelper {

    /**
     * 获取相机数量
     */
    fun getNumberOfCameras():Int{
        return Camera.getNumberOfCameras()
    }

    /**
     * 设置相机预览尺寸
     */
    fun setPreviewSize(cameraParam: Camera.Parameters,context: Context){

    }

    /**
     * 设置相机预览色素
     */
    fun setColorFormat(cameraParam: Camera.Parameters){
        if (cameraParam.supportedPreviewFormats.contains(ImageFormat.NV21)){
            cameraParam.previewFormat = ImageFormat.NV21
        }
    }

    /**
     * 设置相机聚焦方式
     */
    fun setFocusMode(cameraParam: Camera.Parameters){
        val modes = cameraParam.supportedFocusModes?:return
        when{
            modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO) -> cameraParam.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
            modes.contains(Camera.Parameters.FOCUS_MODE_AUTO) -> cameraParam.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            modes.contains(Camera.Parameters.FOCUS_MODE_FIXED) -> cameraParam.focusMode = Camera.Parameters.FOCUS_MODE_FIXED
        }
    }

    /**
     * 设置相机FPS（Frames Per Second）每秒传输帧数
     */
    fun setFPS(){

    }

    /**
     * 设置闪光灯
     */
    fun setFlashMode(cameraParam: Camera.Parameters,mode:String){

    }


    fun setAutoExposureLock(cameraParam: Camera.Parameters,flag:Boolean){

    }

    fun setSceneMode(cameraParam: Camera.Parameters,mode:String){

    }

    fun setAntibanding(cameraParam: Camera.Parameters,mode:String){

    }

    fun setVideoStabilization(cameraParam: Camera.Parameters,flag: Boolean){

    }

}