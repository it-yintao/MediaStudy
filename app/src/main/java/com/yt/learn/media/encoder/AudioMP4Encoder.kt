package com.yt.learn.media.encoder

import android.media.MediaCodec
import android.media.MediaFormat
import com.yt.learn.media.muxer.MMuxer
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1。0
 * 创建日期 ：2020/9/11
 * 描述：采集音频编码成mp4文件件中的音频轨
 */


class AudioMP4Encoder(muxer:MMuxer) :BaseEncoder(muxer) {
    private val TAG = AudioMP4Encoder::class.simpleName



    override fun encodeType(): String {
        return "audio/mp4a-latm"
    }

    override fun configEncoder(codec: MediaCodec) {

    }

    override fun addTrack(muxer: MMuxer, mediaFormat: MediaFormat) {
        muxer.addAudioTrack(mediaFormat)
    }

    override fun writeData(
        muxer: MMuxer,
        byteBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ) {

    }

    override fun release(muxer: MMuxer) {
        muxer.releaseAudioTrack()
    }
}