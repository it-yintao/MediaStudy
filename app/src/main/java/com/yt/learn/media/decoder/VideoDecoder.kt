package com.yt.learn.media.decoder

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import android.view.Surface
import android.view.SurfaceView
import com.yt.learn.media.extractor.IExtractor
import com.yt.learn.media.extractor.VideoExtractor
import java.nio.ByteBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：视频解码器
 */
class VideoDecoder(path:String,sfv:SurfaceView?,surface:Surface?):
    BaseDecoder(path) {

    private val TAG = VideoDecoder::class.simpleName

    private val mSurfaceView = sfv
    private var mSurface = surface

    override fun check(): Boolean {
        if (mSurfaceView == null && mSurface == null){
            Log.w(TAG,"SurfaceView 和 Surface都为空，至少需要一个不为空")
            mStateListener?.decoderError(this,"显示器为空")
            return false
        }
        return true
    }

    override fun initExtractor(path: String): IExtractor {
        return VideoExtractor(path)
    }

    override fun initSpecParams(format: MediaFormat) {

    }

    override fun configCodec(codec: MediaCodec, format: MediaFormat): Boolean {
       mSurface?.let {
           codec.configure(format,mSurface,null,0)
           notifyDecode()
           return true
       }
        mSurfaceView?.let {
            codec.configure(format,it.holder.surface,null,0)
            notifyDecode()
            return true
        }

        return false

    }

    override fun initRender(): Boolean {
       return true
    }

    override fun render(outputBuffer: ByteBuffer, bufferInfo: MediaCodec.BufferInfo) {

    }

    override fun doneDecode() {

    }
}