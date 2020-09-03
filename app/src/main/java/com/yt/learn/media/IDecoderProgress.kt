package com.yt.learn.media

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：解码进度
 */
interface IDecoderProgress {

    /**
     * 视频宽高回调
     */
    fun videoSizeChange(width:Int,height:Int,rotationAngle:Int)

    /**
     * 视频播放进度回调
     */
    fun videoProgressChange(pos:Long)

}