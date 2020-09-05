package com.yt.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.Surface
import com.yt.learn.R
import com.yt.learn.media.decoder.AudioDecoder
import com.yt.learn.media.decoder.VideoDecoder
import com.yt.learn.opengl.IDrawer
import com.yt.learn.opengl.VideoDrawer
import com.yt.learn.opengl.egl.SimpleRender
import kotlinx.android.synthetic.main.activity_opengl_player.*
import java.util.concurrent.Executors

class OpenGLPlayerActivity : AppCompatActivity() {

    lateinit var drawer:IDrawer
    lateinit var videoDecoder: VideoDecoder
    lateinit var audioDecoder: AudioDecoder
    //文件路径
    val path = Environment.getExternalStorageDirectory().absolutePath + "/mvtest.mp4"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_opengl_player)
        initRender()
    }

    private fun initRender(){
        gl_surface.setEGLContextClientVersion(2)
        drawer = VideoDrawer(this)
        drawer.setVideoSize(1920,1080)
        drawer.getSurfaceTexture {
            initPlayer(Surface(it))
        }

        val render = SimpleRender()
        render.addDrawer(drawer)
        gl_surface.setRenderer(render)

    }

    private fun initPlayer(sf:Surface){
        val threadPool = Executors.newFixedThreadPool(6)

        videoDecoder =VideoDecoder(path,null,sf)
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