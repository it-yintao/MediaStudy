package com.yt.learn.media

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：解码状态回调接口
 */
interface IDecoderStateListener {
    fun decoderPrepare(decodeJob:BaseDecoder?)
    fun decoderReady(decodeJob: BaseDecoder?)
    fun decoderRunning(decodeJob: BaseDecoder?)
    fun decoderPause(decodeJob: BaseDecoder?)
    fun decodeOneFrame(decodeJob: BaseDecoder?, frame: Frame)
    fun decoderFinish(decodeJob: BaseDecoder?)
    fun decoderDestroy(decodeJob: BaseDecoder?)
    fun decoderError(decodeJob: BaseDecoder?,msg:String?)
}