package com.yt.learn.opengl.drawer

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.opengl.GLES20.*
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * @author yt
 * 版本 ：1.0
 * 创建日期 ：2020/9/4
 * 描述：从资源文件中加载文本
 */

object ShaderHelper {
    val TAG = ShaderHelper::class.simpleName
    /**
     * 读取资源文件
     */
    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        val body = StringBuilder()
        try {
            val inputStream =
                context.resources.openRawResource(resourceId)
            val inputStreamReader =
                InputStreamReader(inputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var nextLine: String?
            while (bufferedReader.readLine().also { nextLine = it } != null) {
                body.append(nextLine)
                body.append('\n')
            }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId", e)
        } catch (nfe: NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId", nfe)
        }
        return body.toString()
    }

    /**
     * 顶点着色器
     */
    fun compileVertexShader(shaderCode:String):Int{
        return compileShader(
            GL_VERTEX_SHADER,
            shaderCode
        )
    }

    /**
     * 片段着色器
     */
    fun compileFragmentShader(shaderCode: String):Int{
        return compileShader(
            GL_FRAGMENT_SHADER,
            shaderCode
        )
    }

   private fun compileShader(type:Int,shaderCode:String):Int{
        val shaderId = glCreateShader(type)
        if (shaderId == 0){
            Log.i(TAG,"could not create new shader")
            return shaderId
        }
        //将着色器源代码上传到着色器对象里
       glShaderSource(shaderId,shaderCode)
       //编译着色器
       glCompileShader(shaderId)
       //检查openGL是否能成功编译
       val compileStatus = IntArray(1)
       glGetShaderiv(shaderId, GL_COMPILE_STATUS,compileStatus,0)
       //编译失败
       if (compileStatus[0]==0){
           glDeleteShader(shaderId)
           return 0
       }
       return shaderId
    }


    /**
     * 连接到着色器程序
     */
    fun linkProgram(vertexShaderId:Int,fragmentShaderId:Int):Int{
        val programId = glCreateProgram()
        if (programId == 0){
            Log.e(TAG,"could not create new program")
            return 0
        }
        //附上着色器
        glAttachShader(programId,vertexShaderId)
        glAttachShader(programId,fragmentShaderId)
        //将着色器关联程序
        glLinkProgram(programId)

        val linkStatus = IntArray(1)
        glGetProgramiv(programId, GL_LINK_STATUS,linkStatus,0)

        if (linkStatus[0] == 0){
            Log.e(TAG,"linking of program failed")
            glDeleteProgram(programId)
            return 0
        }
        return programId
    }


    /**
     * 编译openGL程序
     */
    fun buildProgram(vertexShaderSource:String,fragmentShaderSource: String):Int{
        val vertexShader =
            compileVertexShader(
                vertexShaderSource
            )
        val fragmentShader =
            compileFragmentShader(
                fragmentShaderSource
            )
        return linkProgram(
            vertexShader,
            fragmentShader
        )
    }


    /**
     * 在使用OpenGL的程序之前，先验证这个程序相对于当前的OpenGL状态是不是不是有效的，
     * 当前程序可能是低效率的、无法运行的等
     */
    fun validateProgram(programId:Int):Boolean{
        glValidateProgram(programId)
        val validateStatus = IntArray(1)
        glGetProgramiv(programId, GL_VALIDATE_STATUS,validateStatus,0)
        Log.v(TAG,"Result of validating program:${validateStatus[0]}\nLog:${glGetProgramInfoLog(programId)}")
        return validateStatus[0] != 0
    }

}