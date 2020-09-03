package com.yt.learn.media

import android.media.MediaCodec
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：一帧数据
 */
class Frame {

    var buffer:ByteBuffer? = null

    var bufferInfo = MediaCodec.BufferInfo()
    private set
    fun setBufferInfo(info:MediaCodec.BufferInfo){
        bufferInfo.set(info.offset,info.size,info.presentationTimeUs,info.flags)
    }
}