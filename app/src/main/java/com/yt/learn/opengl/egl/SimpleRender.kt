package com.yt.learn.opengl.egl

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import com.yt.learn.opengl.OpenGLTools
import com.yt.learn.opengl.drawer.IDrawer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/5
 * 描述：简单的OpenGL渲染器
 */
class SimpleRender :GLSurfaceView.Renderer {

    private val drawers = mutableListOf<IDrawer>()


    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0,0,width,height)
        for (drawer in drawers){
            drawer.setWorldSize(width,height)
        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
       glClearColor(0f,0f,0f,0f)
        //开启混合，即半透明
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

        val textureIds = OpenGLTools.createTextureIds(drawers.size)
        for ((idx,drawer) in drawers.withIndex()){
            drawer.setTextureID(textureIds[idx])
        }
    }

    override fun onDrawFrame(p0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        drawers.forEach{
            it.draw()
        }

    }


    fun addDrawer(drawer: IDrawer){
        drawers.add(drawer)
    }
}