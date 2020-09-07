package com.yt.learn.opengl.drawer

import android.graphics.SurfaceTexture

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/4
 * 描述：渲染器
 */
interface IDrawer {
    fun setVideoSize(videoW:Int,videoH: Int)
    fun setWorldSize(worldW:Int,worldH: Int)
    fun setAlpha(alpha:Float)
    fun draw()

    /**
     * 纹理id
     */
    fun setTextureID(id: Int)
    fun getSurfaceTexture(cb:(st:SurfaceTexture) ->Unit){}
    fun release()
}