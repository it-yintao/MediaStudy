package com.yt.learn.media.encoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import com.yt.learn.media.Frame
import com.yt.learn.media.muxer.MMuxer
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/5
 * 描述：基础编码器
 */
abstract class BaseEncoder(muxer:MMuxer,width:Int = -1,height:Int = -1):Runnable {

    private val TAG = BaseEncoder::class.simpleName

    //目标视频宽，只有视频编码的时候才有效
    protected val mWidth:Int = width

    //目标视频高，只有视频编码的时候才有效
    protected val mHeight:Int = height

    //Mp4合成器
    private var mMuxer:MMuxer = muxer

    //线程运行
    private var mRunning = true

    //编码帧序列
    private var mFrames = mutableListOf<Frame>()

    //编码器
    private lateinit var mCodec:MediaCodec

    //当前编码帧信息
    private val mBufferInfo = MediaCodec.BufferInfo()

    //编码输出缓冲区
    private var mOutputBuffers:Array<ByteBuffer>? = null

    //编码输入缓冲区
    private var mInputBuffers:Array<ByteBuffer>? = null

    private var mLock = Object()

    //是否编码结束
    private var mIsEOS = false

    //编码状态监听器
    private var mStateListener:IEncodeStateListener? = null

    init {
        initCodec()
    }

    /**
     * 初始化编码器
     */
    private fun initCodec(){
        mCodec = MediaCodec.createDecoderByType(encodeType())
        configEncoder(mCodec)
        mCodec.start()
        mOutputBuffers = mCodec.outputBuffers
        mInputBuffers = mCodec.inputBuffers
        Log.i(TAG,"编码器初始化完成")
    }

    override fun run() {
       loopEncode()
        done()
    }

    /**
     * 循环编码
     */
    private fun loopEncode(){
       Log.i(TAG,"开始编码")
        while (mRunning && !mIsEOS){
            val empty = synchronized(mFrames){
                mFrames.isEmpty()
            }
            if (empty){
                justWait()
            }
            if (mFrames.isNotEmpty()){
                val frame = synchronized(mFrames){
                    mFrames.removeAt(0)
                }

                if (encodeManually()){
                    encode(frame)
                }else if (frame.buffer == null){//如果是自动编码(比如视频)，遇到结束帧的时候，直接结束掉
                    Log.e(TAG,"发送编码结束标志")
                    mCodec.signalEndOfInputStream()
                    mIsEOS = true
                }
            }
            drain()
        }
    }

    /**
     * 编码
     */
    private fun encode(frame: Frame){
        val index = mCodec.dequeueInputBuffer(-1)

        /*向编码器输入数据*/
        if (index >= 0){
            val inputBuffer = mInputBuffers?.get(index)
            inputBuffer?.clear()
            frame.buffer?.let {
                inputBuffer?.put(it)
            }

            if (frame.buffer == null || frame.bufferInfo.size <= 0){//小于等于0时，为音频结束符表记
                mCodec.queueInputBuffer(index,0,0,
                frame.bufferInfo.presentationTimeUs,MediaCodec.BUFFER_FLAG_END_OF_STREAM)

            }else{
                frame.buffer?.flip()
                frame.buffer?.mark()
                mCodec.queueInputBuffer(index,0,frame.bufferInfo.size,
                frame.bufferInfo.presentationTimeUs,0)
            }
            frame.buffer?.clear()
        }
    }

    /**
     * 榨干编码输出数据
     */
    private fun drain(){
        loop@ while (!mIsEOS){

        }
    }

    /**
     * 编码结束，释放资源
     */
    private fun done(){

    }

    /**
     * 编码进入等待
     */
    private fun justWait(){

    }

    /**
     * 通知继续编码
     */
    private fun notifyGo(){

    }

    /**
     * 将一帧数据压入队列，等待编码
     */
    fun encodeOneFrame(frame: Frame){

    }

    /**
     * 通知结束编码
     */
    fun endOfStream(){

    }

    /**
     * 设置状态监听器
     */
    fun setStateListener(l:IEncodeStateListener){
        mStateListener = l
    }

    /**
     * 编码类型
     */
    abstract fun encodeType():String

    /**
     * 子类配置编码器
     */
    abstract fun configEncoder(codec:MediaCodec)

    /**
     * 配置mp4音频轨道
     */
    abstract fun addTrack(muxer: MMuxer,mediaFormat: MediaFormat)

    /**
     * 往mp4写入音视频数据
     */
    abstract fun writeData(muxer: MMuxer,byteBuffer: ByteBuffer,bufferInfo: MediaCodec.BufferInfo)

    /**
     * 释放子类资源
     */
    abstract fun release(muxer: MMuxer)

    /**
     * 每一帧排队等待时间
     */
    open fun frameWaitTimeMs() = 20L

    /**
     * 是否手动编码
     * 视频：false 音频：true
     *
     * 注：视频编码器通过Surface,MediaCodec自动完成编码；音频数据需要用户自己压入编码缓存区，完成编码
     */
    open fun encodeManually() = true

}