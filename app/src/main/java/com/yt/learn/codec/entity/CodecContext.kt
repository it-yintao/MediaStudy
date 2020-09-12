package com.yt.learn.codec.entity

import android.content.Context

/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：编码上下文
 * 备注 internal修饰的对象只能在当前的module中使用，其他module使用会找不到这个对象或方法
 */
class CodecContext(ctx: Context,
                   internal var video:Video = Video(),
                   internal var audio:Audio = Audio(),
                   internal var ioContext:IOContext = IOContext(),
                   internal var cameraIndex:CameraW) {










    data class Video(internal var mime:String = "video/avc"){

    }

    data class Audio(internal var mime: String = "audio/mp4a-latm")

    data class IOContext(var path:String? = null)

    /**
     * 编码方式硬编、软编
     */
    enum class CodecType{
        HARD,SOFT
    }



}