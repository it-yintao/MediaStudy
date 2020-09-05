package com.yt.learn.opengl.egl

import android.opengl.GLES20.glGenTextures

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/5
 * 描述：OpenGL 常用方法集合
 */
object OpenGLTools {

    fun createTextureIds(count:Int):IntArray{
        val texture = IntArray(count)
        glGenTextures(count,texture,0)//生成纹理
        return texture
    }
}