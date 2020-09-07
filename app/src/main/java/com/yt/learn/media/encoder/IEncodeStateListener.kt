package com.yt.learn.media.encoder

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/5
 * 描述：编码状态回调接口
 */
interface IEncodeStateListener {

    fun encodeStart(encoder: BaseEncoder)
    fun encodeProgress(encoder: BaseEncoder)
    fun encoderFinish(encoder: BaseEncoder)
}