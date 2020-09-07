package com.yt.learn.media.muxer

import android.media.MediaCodec
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import java.lang.Exception
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/5
 * 描述：音视频封装器
 */
class MMuxer {

    private val TAG = MMuxer::class.simpleName

    private var mPath:String

    private var mMdeiaMuxer:MediaMuxer? = null

    private var mVideoTrackIndex = -1
    private var mAudioTrackIndex = -1

    private var mIsAudioTrackAdd = false
    private var mIsVideoTrackAdd = false

    private var mIsAudioEnd = false
    private var mIsVideoEnd = false

    private var mIsStart = false

    private var mStateListener: IMuxerStateListener? = null

    init {
        val fileName = "LVideo_Test"+".mp4"
        val filePath = Environment.getExternalStorageDirectory().absolutePath.toString()+"/"
        mPath = filePath + fileName
        mMdeiaMuxer = MediaMuxer(mPath,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
    }

    fun addVideoTrack(mediaFormat: MediaFormat){
        if (mIsVideoTrackAdd) return
        mMdeiaMuxer?.let {
            mVideoTrackIndex = it.addTrack(mediaFormat)
        }
        Log.i(TAG,"添加视频轨道")
        mIsVideoTrackAdd = true
        startMuxer()
    }

    fun addAudioTrack(mediaFormat: MediaFormat){
        if (mIsAudioTrackAdd) return
        mMdeiaMuxer?.let {
            mAudioTrackIndex = it.addTrack(mediaFormat)
        }
        Log.i(TAG,"添加音频轨道")
        mIsAudioTrackAdd = true
        startMuxer()
    }


    fun setNoAudio(){
        if (mIsAudioTrackAdd) return
        mIsAudioTrackAdd = true
        mIsAudioEnd = true
        startMuxer()
    }

    fun setNoVideo(){
        if (mIsVideoTrackAdd) return
        mIsVideoTrackAdd = true
        mIsVideoEnd = true
        startMuxer()
    }

    fun writeVideoData(byteBuffer: ByteBuffer,bufferInfo:MediaCodec.BufferInfo){
        if (mIsStart){
            mMdeiaMuxer?.writeSampleData(mVideoTrackIndex,byteBuffer,bufferInfo)
        }
    }

    fun writeAudioData(byteBuffer: ByteBuffer,bufferInfo: MediaCodec.BufferInfo){
        if (mIsStart){
            mMdeiaMuxer?.writeSampleData(mAudioTrackIndex,byteBuffer,bufferInfo)
        }
    }

    private fun startMuxer(){
        if (mIsAudioTrackAdd && mIsVideoTrackAdd){
            mMdeiaMuxer?.start()
            mIsStart = true
            mStateListener?.onMuxerStart()
            Log.i(TAG,"启动分装器")
        }
    }

    fun releaseVideoTrack(){
        mIsVideoEnd = true
        release()
    }

    fun releaseAudioTrack(){
        mIsAudioEnd = true
        release()
    }

    private fun release(){
        if (mIsAudioEnd && mIsVideoEnd){
            mIsAudioTrackAdd = false
            mIsVideoTrackAdd = false
            try {
                mMdeiaMuxer?.stop()
                mMdeiaMuxer?.release()
                mMdeiaMuxer = null
                Log.i(TAG,"退出封装器")
            }catch (e:Exception){
                e.printStackTrace()
            }finally {
                mStateListener?.onMuxerFinish()
            }
        }
    }

    fun setStateListener(l:IMuxerStateListener){
        this.mStateListener = l
    }

    interface IMuxerStateListener{
        fun onMuxerStart()
        fun onMuxerFinish()
    }
}