package com.yt.learn.media.record


import android.media.AudioRecord
import android.util.Log
import com.yt.learn.media.Constants.DEFAULT_AUDIO_FORMAT
import com.yt.learn.media.Constants.DEFAULT_CHANNEL_CONFIG
import com.yt.learn.media.Constants.DEFAULT_SAMPLE_RATE
import com.yt.learn.media.Constants.DEFAULT_SOURCE

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/10
 * 描述：音频采集
 */

class AudioCapture {
    private val TAG = AudioCapture::class.simpleName

    var mIsCaptureStarted = false
    var mIsLoopExit = false
    var mMinBufferSize = 0
    var mAudioRecord:AudioRecord? = null
    var mCaptureThread:Thread? = null
    var mAudioFrameCapturedListener:OnAudioFrameCapturedListener? = null

    fun setAudioFrameCapturedListener(listener: OnAudioFrameCapturedListener){
        mAudioFrameCapturedListener = listener
    }

    fun startCapture(audioSource:Int = DEFAULT_SOURCE,sampleRateInHZ:Int= DEFAULT_SAMPLE_RATE,
    channelConfig:Int = DEFAULT_CHANNEL_CONFIG,audioFormat:Int = DEFAULT_AUDIO_FORMAT ):Boolean{
        if (mIsCaptureStarted){
            Log.e(TAG,"Capture already started")
            return false
        }
        mMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHZ,channelConfig, audioFormat)
        if (mMinBufferSize == AudioRecord.ERROR_BAD_VALUE){
            Log.e(TAG,"Invalid parameter")
            return false
        }
        Log.d(TAG,"getMinBufferSize = $mMinBufferSize bytes!")
        mAudioRecord = AudioRecord(audioSource,sampleRateInHZ,channelConfig,audioFormat,mMinBufferSize)
        if (mAudioRecord?.state == AudioRecord.STATE_UNINITIALIZED){
            Log.e(TAG,"AudioRecord initialize fail")
            return false
        }

        mAudioRecord?.startRecording()//开始采集

        //开启一个线程读取采集的音频数据
        mIsLoopExit = false
        mCaptureThread = Thread(AudioCaptureRunnable())
        mCaptureThread?.start()
        mIsCaptureStarted = true
        Log.d(TAG,"start audio capture success")
        return true

    }

    fun stopCapture(){
        if (!mIsCaptureStarted){
            return
        }
        mIsLoopExit = true
        try {
            mCaptureThread?.interrupt()
            mCaptureThread?.join(1000)
        }catch (e:InterruptedException){
            e.printStackTrace()
        }
        if (mAudioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING){
            mAudioRecord?.stop()
        }
        mAudioRecord?.release()
        mIsCaptureStarted = false
        mAudioFrameCapturedListener = null
        Log.d(TAG,"stop audio capture success")
    }

    inner class AudioCaptureRunnable :Runnable{
        override fun run() {
            while (!mIsLoopExit){
                val  buffer = ByteArray(mMinBufferSize)
                when(val ret = mAudioRecord?.read(buffer,0,mMinBufferSize)){
                    AudioRecord.ERROR_INVALID_OPERATION ->{
                        Log.e(TAG,"error ERROR_INVALID_OPERATION ")
                    }
                    AudioRecord.ERROR_BAD_VALUE ->{
                        Log.e(TAG,"error ERROR_BAD_VALUE")
                    }
                    else ->{
                        mAudioFrameCapturedListener?.onAudioFrameCapture(buffer);
                        Log.d(TAG,"OK,captured $ret bytes")
                    }
                }
            }
        }

    }

    interface OnAudioFrameCapturedListener{
      fun onAudioFrameCapture(audioData:ByteArray)
    }
}
