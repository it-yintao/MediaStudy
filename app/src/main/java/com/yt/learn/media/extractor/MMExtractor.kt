package com.yt.learn.media.extractor

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：音视频轨道分离器
 */
class MMExtractor(path:String) {

    /*音视频分离器*/
    private var mExtractor:MediaExtractor? = null

    /*音频通道索引*/
    private var mAudioTrack = -1

    /*视频通道索引*/
    private var mVideoTrack = -1

    /*当前帧时间戳*/
    private var mCurSampleTime: Long = 0

    /*当前帧标志*/
    private var mCurSampleFlag:Int = 0

    /*开始解码时间点*/
    private var mStartPos:Long = 0

    init {
        mExtractor = MediaExtractor()
        mExtractor?.setDataSource(path)
    }

    /**
     * 获取视频格式参数
     */
    fun getVideoFormat():MediaFormat?{
        var mediaFormat:MediaFormat? = null
        mExtractor?.let {mediaExtractor ->
            for (i in 0 until mediaExtractor.trackCount){
               val mime = mediaExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME)
               var isVideo = false
                mime?.let {
                    isVideo = it.startsWith("video/")
                }
                if (isVideo){
                    mVideoTrack = i
                    break
                }
            }
            mediaFormat = if (mVideoTrack >= 0) mediaExtractor.getTrackFormat(mVideoTrack) else null

        }
        return mediaFormat
    }


    /**
     * 获取音频格式参数
     */
    fun getAudioFormat():MediaFormat?{
        var mediaFormat:MediaFormat? = null
        mExtractor?.let {mediaExtractor ->
            for (i in 0 until mediaExtractor.trackCount){
                val mime = mediaExtractor.getTrackFormat(i).getString(MediaFormat.KEY_MIME)
                var isAudio = false
                mime?.let {
                    isAudio = it.startsWith("audio/")
                }
                if (isAudio){
                    mAudioTrack = i
                    break
                }
            }
            mediaFormat = if (mAudioTrack >= 0) mediaExtractor.getTrackFormat(mAudioTrack) else null

        }
        return mediaFormat
    }


    /**
     * 读取音视频数据
     */
    fun readBuffer(byteBuffer: ByteBuffer):Int{
        byteBuffer.clear()
        selectSourceTrack()
        var  readSampleCount = -1
        mExtractor?.let {
             readSampleCount = it.readSampleData(byteBuffer,0)
            if (readSampleCount < 0){
                return -1
            }
            mCurSampleFlag = it.sampleFlags
            mCurSampleTime = it.sampleTime
            //进入下一帧
            it.advance()
        }
        return readSampleCount
    }

    /**
     * 选择轨道
     */
    private fun selectSourceTrack(){
        mExtractor?.let {
            if (mVideoTrack >= 0){
                it.selectTrack(mVideoTrack)
            }else if (mAudioTrack >= 0){
                it.selectTrack(mAudioTrack)
            }
        }
    }

    /**
     * seek到指定的位置，并返回实际帧的时间戳
     */
    fun seek(pos:Long):Long{
        var sampleTime = -1L
        mExtractor?.let {
            it.seekTo(pos,MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            sampleTime = it.sampleTime
        }
        return sampleTime
    }

    /**
     * 停止读取数据
     */
    fun stop(){
        mExtractor?.release()
        mExtractor = null
    }

    fun getVideoTrack():Int{
        return mVideoTrack
    }

    fun getAudioTrack():Int{
        return mAudioTrack
    }

    fun setStartPos(pos:Long){
        mStartPos = pos
    }

    /**
     * 获取当前帧时间
     */
    fun getCurrentTimestamp():Long{
        return mCurSampleTime
    }

    fun getSampleFlag():Int{
        return mCurSampleFlag
    }
}