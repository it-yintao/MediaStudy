package com.yt.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Surface
import com.yt.learn.R
import com.yt.learn.opengl.IDrawer
import com.yt.learn.opengl.VideoDrawer
import kotlinx.android.synthetic.main.activity_opengl_player.*

class OpenGLPlayerActivity : AppCompatActivity() {

    lateinit var drawer:IDrawer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_player)
        initRender()
    }

    private fun initRender(){
        drawer = VideoDrawer(this)
        drawer.setVideoSize(1920,1080)
        drawer.getSurfaceTexture {
//            initPlayer(Surface(it))
        }
        gl_surface.setEGLContextClientVersion(2)
//        val render =

    }
}