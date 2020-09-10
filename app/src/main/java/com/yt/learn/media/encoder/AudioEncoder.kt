package com.yt.learn.media.encoder


import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.os.Build
import android.util.Log
import com.yt.learn.media.muxer.MMuxer
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/10
 * 描述：音频编码,业务层需要根据业务来编写、如采集的音频数据合成mp4文件，或者读取视频文件重新编码
 */
//编码采样率
val DEST_SAMPLE_RATE = 44100

//编码码率
private val DEST_BIT_RATE = 128000

class AudioEncoder(muxer: MMuxer):BaseEncoder(muxer){

    private val TAG = AudioEncoder::class.simpleName

    override fun encodeType(): String {
        return "audio/mp4a-latm"
    }

    override fun configEncoder(codec: MediaCodec) {
        //设置双声道
        val audioFormat = MediaFormat.createAudioFormat(encodeType(), DEST_SAMPLE_RATE,2)
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, DEST_BIT_RATE)
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE,100*1024)
        try {
            configEncoderWithCQ(codec,audioFormat)
        }catch (e:Exception){
            e.printStackTrace()
            try {
                configEncoderWithVBR(codec,audioFormat)
            }catch (e:Exception){
                e.printStackTrace()
                Log.e(TAG,"配置编码器失败")
            }
        }
    }

    private fun configEncoderWithCQ(codec: MediaCodec,outputFormat: MediaFormat){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
          outputFormat.setInteger(
              MediaFormat.KEY_BITRATE_MODE,
              MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_CQ
          )
        }
        codec.configure(outputFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)
    }

    private fun configEncoderWithVBR(codec: MediaCodec,outputFormat:MediaFormat){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            outputFormat.setInteger(
                MediaFormat.KEY_BITRATE_MODE,
                MediaCodecInfo.EncoderCapabilities.BITRATE_MODE_VBR
            )
        }
        codec.configure(outputFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE)
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