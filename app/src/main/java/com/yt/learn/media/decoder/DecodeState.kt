package com.yt.learn.media.decoder

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/3
 * 描述：解码状态
 */
enum class DecodeState {
    /*开始状态*/
    START,
    /*解码中*/
    DECODING,
    /*解码暂停*/
    PAUSE,
    /*正在快进*/
    SEEKING,
    /*解码完成*/
    FINISH,
    /*解码器释放*/
    STOP
}