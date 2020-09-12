package com.yt.learn.opengl

import android.content.Context
import android.graphics.PointF
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import com.yt.learn.opengl.drawer.VideoDrawer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/4
 * 描述：自定义GLSurfaceView
 */
class DefGLSurfaceView :GLSurfaceView{

    constructor(context:Context):super(context)

    constructor(context: Context,attr: AttributeSet):super(context,attr)

    private var mPrePoint = PointF()

    private var mDrawer: VideoDrawer? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN ->{
                mPrePoint.x = event.x
                mPrePoint.y = event.y
            }
            MotionEvent.ACTION_MOVE ->{
                val dx = (event.x - mPrePoint.x) / width
                val dy = (event.y - mPrePoint.y) / height
                mDrawer?.translate(dx,dy)
                mPrePoint.x = event.x
                mPrePoint.y = event.y
            }
        }
        return true
    }

    fun addDrawer(drawer: VideoDrawer){
        mDrawer = drawer
    }
}