package com.yt.learn.media

import com.yt.learn.media.Constants.DEFAULT_AUDIO_FORMAT
import com.yt.learn.media.Constants.DEFAULT_CHANNEL_CONFIG
import com.yt.learn.media.Constants.DEFAULT_SAMPLE_RATE
import com.yt.learn.media.Constants.DEFAULT_SOURCE

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/11
 * 描述：媒体参数
 */
data class MediaParam (var audioSource:Int = DEFAULT_SOURCE,var sampleRateInHz:Int = DEFAULT_SAMPLE_RATE,
var channelConfig:Int = DEFAULT_CHANNEL_CONFIG,var audioFormat:Int = DEFAULT_AUDIO_FORMAT)