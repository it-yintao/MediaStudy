package com.yt.learn.media.encoder

import android.media.MediaCodec
import android.media.MediaCodec.CONFIGURE_FLAG_ENCODE
import android.media.MediaFormat
import com.yt.learn.media.Constants.DEFAULT_AUDIO_FORMAT
import com.yt.learn.media.Constants.DEFAULT_BITRATE
import com.yt.learn.media.Constants.DEFAULT_CHANNEL_NUM
import com.yt.learn.media.Constants.DEFAULT_MAX_BUFFER_SIZE
import com.yt.learn.media.Constants.DEFAULT_PROFILE_LEVEL
import com.yt.learn.media.Constants.DEFAULT_SAMPLE_RATE
import com.yt.learn.media.muxer.MMuxer
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/11
 * 描述：采集音频编码成mp4文件件中的音频轨
 */


class AudioMP4Encoder(muxer:MMuxer) :BaseEncoder(muxer) {
    private val TAG = AudioMP4Encoder::class.simpleName



    override fun encodeType(): String {
        return "audio/mp4a-latm"
    }

    override fun configEncoder(codec: MediaCodec) {
        //为音频编码器设置编码格式
        val audioFormat = MediaFormat.createAudioFormat(encodeType(), DEFAULT_SAMPLE_RATE,
            DEFAULT_CHANNEL_NUM)
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, DEFAULT_BITRATE)
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE,DEFAULT_PROFILE_LEVEL)
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, DEFAULT_MAX_BUFFER_SIZE)

        codec.configure(audioFormat,null,null,CONFIGURE_FLAG_ENCODE)

    }

    override fun addTrack(muxer: MMuxer, mediaFormat: MediaFormat) {
        muxer.addAudioTrack(mediaFormat)
    }

    override fun writeData(
        muxer: MMuxer,
        byteBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ) {
        muxer.writeAudioData(byteBuffer, bufferInfo)
    }

    override fun release(muxer: MMuxer) {
        muxer.releaseAudioTrack()
    }

    override fun frameWaitTimeMs(): Long {
        return 5
    }
}