package com.yt.learn.codec.texture.impl


import android.opengl.GLES20
import android.opengl.GLES30
import com.yt.learn.codec.texture.Texture
import java.lang.RuntimeException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer


/**
 * @author yt
 * 版本 ：1.1
 * 创建日期 ：2020/9/12
 * 描述：纹理
 */
abstract class BaseTexture(var textureId:IntArray,var name: String = "BaseTexture"): Texture {


    companion object{
        var COORDS_PER_VERTEX = 2
        var TEXTURE_COORDS_PER_VERTEX = 2
        val COORDS_BYTE_SIZE = COORDS_PER_VERTEX * 4 * 4
    }

    private val lock = Any()
    private var enableVAO = false
    var shaderProgram:Int? = null
    var drawer:GLDrawer = GLDrawer()
    private var position:FloatBuffer? = null //native中的顶点数组数据
    private var texCoordinate:FloatBuffer? = null//native中的纹理坐标数据
    private var requestUpdateLocation:Boolean = false
    private var vbos:IntArray = IntArray(1)//VBO(Vertex buffer object),
    private var vao:IntArray?=null //VAO(Vertex Array object)


    init {
        updateLocation(
            floatArrayOf(
            //纹理坐标
            0f, 0f,//LEFT,BOTTOM
            1f, 0f,//RIGHT,BOTTOM
            0f, 1f,//LEFT,TOP
            1f, 1f//RIGHT,TOP
        ),
            //顶点坐标
            floatArrayOf(
            -1f, -1f,//LEFT,BOTTOM
            1f, -1f,//RIGHT,BOTTOM
            -1f, 1f,//LEFT,TOP
            1f, 1f//RIGHT,TOP
        ))
    }

    override fun init() {
        createVBOs()
    }

    /**
     * 在显卡存储空间里一块缓存区BUFFER，用于存储顶点即其他属性相关的信息
     * （顶点信息，颜色信息，纹理坐标信息等）
     */
    private fun createVBOs(){
        //简单理解生成vob对象
        GLES20.glGenBuffers(vbos.size,vbos,0)
        //bind a named buffer object
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vbos[0])
        //vob变成了一个顶点缓冲类型
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, COORDS_BYTE_SIZE * 2,null,GLES20.GL_STATIC_DRAW)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_NONE)
    }

    /**
     * 更新VBO缓存区
     */
    private fun updateVBOS(){
        synchronized(lock){
            if (!requestUpdateLocation)return
            requestUpdateLocation = false
        }

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER,vbos[0])
        //用来更新一个已有缓冲区对象中的一部分数据
        position?.let {
            GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER,0,COORDS_BYTE_SIZE,it)
        }
       texCoordinate?.let {
           //在前面的内容一方了位置信息
           GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, COORDS_BYTE_SIZE, COORDS_BYTE_SIZE,it)
       }
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_NONE)
    }

    /**
     * 为纹理坐标和opengl的顶点坐标，开辟native内存并将java数据存储到开辟的内存中
     * @param array 顶点坐标数组或纹理坐标数组
     */
    private fun createShapeVerticesBuffer(array: FloatArray):FloatBuffer{
        //开辟native内存
        val result = ByteBuffer.allocateDirect(4 * array.size)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        //将java的数组数据拷贝到已开辟的内存里
        result.put(array)
            //设置result的其实位置
            .position(0)
        return result
    }

    fun createProgram(vertex:String,fragment:String):Int{
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER,vertex)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER,fragment)
        return linkProgram(vertexShader,fragmentShader)
    }

    /**
     * 加载着色器
     * @param type GL_VERTEX_SHADER代表生成顶点着色器，GL_FRAGMENT_SHADER代表生成片段着色器
     * @param shaderSource 着色器代码
     */
    private fun loadShader(type:Int,shaderSource:String):Int{
        //创建Shader
        val shader = GLES20.glCreateShader(type)
        if (shader == 0){
            throw RuntimeException("create shader failed ${GLES20.glGetError()}")
        }
        //加载Shader代码
        GLES20.glShaderSource(shader,shaderSource)
        //编译Shader
        GLES20.glCompileShader(shader)
        return shader
    }

    /**
     * 将俩个Shader连接到program中
     */
    private fun linkProgram(verShader:Int,fragShader:Int):Int{
        //创建program
        val program = GLES20.glCreateProgram()
        if (program == 0){
            throw RuntimeException("create Program failed ${GLES20.glGetError()}")
        }
        //附着顶点和片段着色器
        GLES20.glAttachShader(program,verShader)
        GLES20.glAttachShader(program,fragShader)
        //链接program
        GLES20.glLinkProgram(program)
        //告诉OpenGL ES使用此program
        GLES20.glUseProgram(program)
        return program
    }

    /**
     * 更新s,t,x,y
     * @param texCoordinate 纹理坐标
     * @param position 顶点坐标
     */
    fun updateLocation(texCoordinate:FloatArray,position: FloatArray){
        this.position = createShapeVerticesBuffer(position)
        this.texCoordinate = createShapeVerticesBuffer(texCoordinate)
        synchronized(lock){
            this.requestUpdateLocation = true
        }
    }

    /**
     * 创建顶点数组
     */
    private fun enableVAO(posLoc:Int,texLoc:Int){

        if (null == vao) {
            vao = IntArray(1)
            vao?.let{
                GLES30.glGenVertexArrays(it.size, it, 0)
                GLES30.glBindVertexArray(it[0])
                GLES30.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbos[0])

                GLES30.glEnableVertexAttribArray(posLoc)
                GLES30.glEnableVertexAttribArray(texLoc)

                GLES30.glVertexAttribPointer(posLoc, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    COORDS_PER_VERTEX * 4, 0)
                GLES30.glVertexAttribPointer(texLoc, TEXTURE_COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    TEXTURE_COORDS_PER_VERTEX * 4, COORDS_BYTE_SIZE)
                GLES30.glBindVertexArray(GLES30.GL_NONE)

            }
        }

        vao?.let {
            GLES30.glBindVertexArray(it[0])
        }


    }

    class GLDrawer{
        fun draw(){
            //OpenGl开始绘制
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4)
        }
    }
}