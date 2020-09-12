package com.yt.learn.codec.texture

/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：纹理绘制接口
 */
interface Texture {
    fun init()
    fun draw(transformMatrix:FloatArray?)
}