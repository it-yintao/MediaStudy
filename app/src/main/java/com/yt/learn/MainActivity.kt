package com.yt.learn

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(),View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bt_simple_player.setOnClickListener(this)
        bt_opengl_player.setOnClickListener(this)
        //请求权限
        val perms = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO )
        if(!EasyPermissions.hasPermissions(this,*perms)){
            EasyPermissions.requestPermissions(this,"需要读写手机SD卡权限、音频录制权限",1000,*perms)
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.bt_simple_player -> {
                startActivity(Intent(this,SimplePlayerActivity::class.java))
            }
            R.id.bt_opengl_player ->{
                startActivity(Intent(this,
                    OpenGLPlayerActivity::class.java))
            }
        }
    }
}
