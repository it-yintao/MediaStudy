package com.yt.learn.media.encoder

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import android.view.Surface
import com.yt.learn.media.muxer.MMuxer
import java.lang.IllegalArgumentException
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/10
 * 描述：视频编码器
 */

const val DEFAULT_ENCODE_FRAME_RATE = 30
class VideoEncoder(muxer:MMuxer,width:Int,height:Int):BaseEncoder(muxer, width, height) {
    private val TAG = VideoEncoder::class.simpleName

    private var mSurface: Surface? = null

    override fun encodeType(): String {
       return "video/avc"
    }

    override fun configEncoder(codec: MediaCodec) {
       if (mWidth <=0 || mHeight <= 0){
           throw IllegalArgumentException("Encode width or height is invalid,width:$mWidth,height:$mHeight")
       }
        val bitrate = 3 * mWidth * mHeight
        val outputFormat = MediaFormat.createAudioFormat(encodeType(),mWidth,mHeight)
        outputFormat.setInteger(MediaFormat.KEY_BIT_RATE ,bitrate)
        outputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, DEFAULT_ENCODE_FRAME_RATE)
        outputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1)
        outputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT,MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface)

        try {
            configEncoderWithCQ(codec, outputFormat)
        } catch (e: Exception) {
            e.printStackTrace()
            // 捕获异常，设置为系统默认配置 BITRATE_MODE_VBR
            try {
                configEncoderWithVBR(codec, outputFormat)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "配置视频编码器失败")
            }
        }

        mSurface = codec.createInputSurface()
    }

    private fun configEncoderWithCQ(codec: MediaCodec, outputFormat: MediaFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 本部分手机不支持 BITRATE_MODE_CQ 模式，有可能会异常
            outputFormat.setInteger(
                MediaFormat.KEY_BITRATE_MODE,
                MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ
            )
        }
        codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    private fun configEncoderWithVBR(codec: MediaCodec, outputFormat: MediaFormat) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outputFormat.setInteger(
                MediaFormat.KEY_BITRATE_MODE,
                MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
            )
        }
        codec.configure(outputFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    override fun addTrack(muxer: MMuxer, mediaFormat: MediaFormat) {
       muxer.addVideoTrack(mediaFormat)
    }

    override fun writeData(
        muxer: MMuxer,
        byteBuffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo
    ) {
        muxer.writeVideoData(byteBuffer, bufferInfo)
    }

    override fun release(muxer: MMuxer) {
       muxer.releaseVideoTrack()
    }

    fun getEncodeSurface():Surface?{
        return mSurface
    }
}