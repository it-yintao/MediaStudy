package com.yt.learn.media

import android.media.AudioFormat
import android.media.MediaRecorder

/**
 * @author yt
 * 版本 ：1。0
 * 创建日期 ：2020/9/11
 * 描述：音视频常量
 */
object Constants {
    const val DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC
    const val DEFAULT_SAMPLE_RATE = 44100
    const val DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    const val DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    const val DEFAULT_BITRATE = 128 * 1000 //AAC-LC, 64*1024 for AAC-HE
}