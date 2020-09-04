package com.yt.learn.opengl

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.Matrix
import com.yt.learn.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/4
 * 描述：视频渲染器
 */
class VideoDrawer(context: Context) :IDrawer {

    private val mContext = context

    //设置定点坐标
    private val mVertexCoors = floatArrayOf(
        -1f,-1f,
        1f,-1f,
        -1f,1f,
        1f,1f
    )

    //纹理坐标
    private val mTextureCoors = floatArrayOf(
        0f,1f,
        1f,1f,
        0f,0f,
        1f,0f
    )

    private var mWorldWidth:Int = -1
    private var mWorldHeight:Int = -1
    private var mVideoWidth:Int = -1
    private var mVideoHeight:Int = -1

    private var mTextureId :Int = -1

    //纹理坐标
    private var mSurfaceTexture:SurfaceTexture? = null

    //委托函数
    private var mSftCb:((SurfaceTexture) -> Unit)? = null

    //OpenGL程序ID
    private var mProgram:Int = -1

    //矩阵变换接收者
    private var mVertexMatrixHandler:Int = -1
    //顶点坐标接收者
    private var mVertexPosHandler:Int = -1
    //纹理坐标接收者
    private var mTexturePosHandler:Int = -1
    //纹理接收者
    private var mTextureHandler:Int = -1
    //半透值接收者
    private var mAlphaHandler:Int = -1

    private lateinit var mVertexBuffer:FloatBuffer
    private lateinit var mTextureBuffer: FloatBuffer

    private var mMatrix:FloatArray? = null

    private var mAlpha = 1f

    init {
        //步骤1、初始化定点坐标
        initPos()
    }

    private fun initPos(){
        //开辟native内存
        mVertexBuffer = ByteBuffer.allocateDirect(mVertexCoors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        //Java的数据复制到本地内存
        mVertexBuffer.put(mVertexCoors)
        //设置mVertexBuffer初始位置
        mVertexBuffer.position(0)

        mTextureBuffer = ByteBuffer.allocateDirect(mTextureCoors.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        mTextureBuffer.put(mTextureCoors)
        mTextureBuffer.position(0)

    }

    private var mWidthRatio = 1f
    private var mHeightRatio = 1f

    /**
     * 初始化矩阵
     */
    private fun initDefMatrix(){
        if (mMatrix!=null)return
        if (mVideoWidth != -1 && mVideoHeight != -1 && mWorldWidth != -1 && mWorldHeight != -1){
            mMatrix = FloatArray(16)
            var prjMatrix = FloatArray(16)

            val originRatio = mVideoWidth / mVideoHeight.toFloat()
            val worldRatio = mWorldWidth / mWorldHeight.toFloat()


            if(mWorldWidth > mWorldHeight){//横屏
                if (originRatio > worldRatio){
                    Matrix.orthoM(
                        prjMatrix,0,
                        -mWidthRatio,mWidthRatio,
                        -mHeightRatio,mHeightRatio,
                        3f,5f
                    )
                }else{//原始比例小于窗口比例，缩放高度会导致高度超出，因此，高度以窗口为准，缩放宽度
                    mWidthRatio = worldRatio/originRatio
                    Matrix.orthoM(
                        prjMatrix,0,
                        -mWidthRatio,mWidthRatio,
                        -mHeightRatio,mHeightRatio,
                        3f,5f
                    )
                }
            }else{
                if (originRatio > worldRatio){
                    mHeightRatio = originRatio / worldRatio
                    Matrix.orthoM(
                        prjMatrix,0,
                        -mWidthRatio,mWidthRatio,
                        -mHeightRatio,mHeightRatio,
                        3f,5f
                    )
                }else{
                    mWidthRatio = worldRatio / originRatio
                    Matrix.orthoM(
                        prjMatrix, 0,
                        -mWidthRatio, mWidthRatio,
                        -mHeightRatio, mHeightRatio,
                        3f, 5f
                    )
                }
            }

            //设置相机位置
            val viewMatrix = FloatArray(16)
            Matrix.setLookAtM(
                viewMatrix,0,
                0f,0f,5.0f,
                0f,0f,0f,
                0f,1.0f,0f
            )
            //计算变换矩阵
            Matrix.multiplyMM(mMatrix,0,prjMatrix,0,viewMatrix,0)
        }
    }

    override fun setVideoSize(videoW: Int, videoH: Int) {
        mVideoWidth = videoW
        mVideoHeight = videoH
    }

    override fun setWorldSize(worldW: Int, worldH: Int) {
        mWorldWidth = worldW
        mVideoHeight = worldH
    }

    override fun setAlpha(alpha: Float) {
        mAlpha = alpha
    }

    override fun draw() {
        if (mTextureId != -1){
            initDefMatrix()
            //步骤2：创建、编译并启动OpenGL着色器
            createGLPrg()
            //步骤3：激活并绑定纹理单元
            activateTexture()
            //步骤4:绑定图片到纹理单元
            updateTexture()
            //步骤5：开始渲染绘制
            doDraw()
        }
    }

    private fun createGLPrg(){
        val vertexShaderSource = ShaderHelper.readTextFileFromResource(mContext, R.raw.texture_vertex_shader)
        val fragmentShaderSource = ShaderHelper.readTextFileFromResource(mContext,R.raw.texture_fragment_shader)

//        val vertexShaderId = ShaderHelper.compileVertexShader(vertexShaderSource)
//        val fragmentShaderId = ShaderHelper.compileFragmentShader(fragmentShaderSource)
//        mProgram = ShaderHelper.linkProgram(vertexShaderId = vertexShaderId,fragmentShaderId = fragmentShaderId )
        //替代上面三行代码，效果一样
        mProgram = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource)
        if (mProgram != 0){
            mVertexMatrixHandler = glGetUniformLocation(mProgram,"uMatrix")
            mVertexPosHandler = glGetAttribLocation(mProgram,"aPosition")
            mTextureHandler = glGetUniformLocation(mProgram,"uTexture")
            mTexturePosHandler = glGetAttribLocation(mProgram,"aCoordinate")
            mAlphaHandler = glGetAttribLocation(mProgram,"alpha")

            if (ShaderHelper.validateProgram(mProgram)){
                //运行openGL程序
                glUseProgram(mProgram)
            }

        }
    }

    private fun activateTexture(){
        //激活指定纹理单元
        glActiveTexture(GL_TEXTURE0)
        //绑定纹理ID到纹理单元
        glBindTexture(GL_TEXTURE_EXTERNAL_OES,mTextureId)
        //将激活的纹理单元传递到着色器里面
        glUniform1f(mTextureHandler,0f)

        //配置边缘过渡参数
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)

    }

    private fun updateTexture(){
        mSurfaceTexture?.updateTexImage()
    }

    private fun doDraw(){
        //启用顶点的句柄
        glEnableVertexAttribArray(mVertexPosHandler)
        glEnableVertexAttribArray(mTextureHandler)
        glUniformMatrix4fv(mVertexMatrixHandler,1,false,mMatrix,0)
        //设置着色器参数，第二个参数表示一个顶点包含的数据数量，这里为xy，所以为2
        glVertexAttribPointer(mVertexPosHandler,2, GL_FLOAT,false,0,mVertexBuffer)
        glVertexAttribPointer(mTexturePosHandler,2, GL_FLOAT,false,0,mTextureBuffer)
        glVertexAttrib1f(mAlphaHandler,mAlpha)

        //开始绘制
        glDrawArrays(GL_TRIANGLE_STRIP,0,4)
    }

    override fun setTextureID(id: Int) {
       mTextureId = id
        mSurfaceTexture = SurfaceTexture(id)
        mSurfaceTexture?.let {
            mSftCb?.invoke(it)
        }
    }

    override fun getSurfaceTexture(cb: (st: SurfaceTexture) -> Unit) {
        mSftCb = cb
    }

    override fun release() {
        glDisableVertexAttribArray(mVertexPosHandler)
        glDisableVertexAttribArray(mTexturePosHandler)
        glBindTexture(GLES20.GL_TEXTURE_2D,0)
        glDeleteTextures(1, intArrayOf(mTextureId),0)
        glDeleteProgram(mProgram)
    }

    fun translate(dx:Float,dy:Float){
        Matrix.translateM(mMatrix,0,dx * mWidthRatio * 2,-dy * mHeightRatio * 2,0f)
    }

    fun scale(sx: Float,sy: Float){
        Matrix.scaleM(mMatrix,0,sx,sy,1f)
        mWidthRatio /= sx
        mHeightRatio /= sy
    }

}