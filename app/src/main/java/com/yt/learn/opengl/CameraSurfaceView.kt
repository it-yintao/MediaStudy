package com.yt.learn.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/12
 * 描述：相机预览的的View
 */
class CameraSurfaceView :GLSurfaceView ,SurfaceTexture.OnFrameAvailableListener{
    constructor(context:Context):super(context)

    constructor(context: Context,attr:AttributeSet):super(context,attr)

    init {
        setEGLContextClientVersion(2)
    }


    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
            requestRender()
    }


}