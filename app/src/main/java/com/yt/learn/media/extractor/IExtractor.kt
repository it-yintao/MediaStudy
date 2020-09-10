package com.yt.learn.media.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：音视频分离器
 */
interface IExtractor {

    fun getFormat():MediaFormat?

    /**
     * 读取音视频数据
     */
    fun readBuffer(byteBuffer: ByteBuffer):Int

    /**
     * 获取当前帧时间
     */
    fun getCurrentTimestamp():Long

    fun getSampleFlag():Int

    /**
     * Seek到指定位置，并返回实际帧的时间戳
     */
    fun seek(pos:Long):Long

    fun setStartPos(pos: Long)

    /**
     * 停止读取数据
     */
    fun stop()
}