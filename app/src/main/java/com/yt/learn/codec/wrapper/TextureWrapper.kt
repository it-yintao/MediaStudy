package com.yt.learn.codec.wrapper

import android.graphics.SurfaceTexture
import com.yt.learn.codec.texture.impl.BaseTexture

/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：
 */
class TextureWrapper(open var surfaceTexture: SurfaceTexture?=null,
                     var texture: BaseTexture?=null) {
}