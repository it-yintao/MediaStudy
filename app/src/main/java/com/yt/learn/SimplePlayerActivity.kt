package com.yt.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import com.yt.learn.media.BaseDecoder
import com.yt.learn.media.decoder.AudioDecoder
import com.yt.learn.media.decoder.VideoDecoder
import kotlinx.android.synthetic.main.activity_simple_player.*
import java.util.concurrent.Executors

class SimplePlayerActivity : AppCompatActivity() {

    //文件路径
    val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
    lateinit var videoDecoder: VideoDecoder
    lateinit var audioDecoder: AudioDecoder



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simple_player)

        initPlayer()
    }

    private fun initPlayer(){
        //采用线程池管理播发音频的线程和播放视频的线程
        val threadPool =  Executors.newFixedThreadPool(6)

        videoDecoder = VideoDecoder(path,sfv,null)
        threadPool.execute(videoDecoder)
        audioDecoder = AudioDecoder(path)
        threadPool.execute(audioDecoder)

        videoDecoder.goOn()
        audioDecoder.goOn()
    }


    override fun onDestroy() {
        videoDecoder.stop()
        audioDecoder.stop()
        super.onDestroy()
    }
}