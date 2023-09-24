package com.example.chatingappver2.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.chatingappver2.R
import com.example.chatingappver2.contract.activity.ViewFullScreenContract
import com.example.chatingappver2.presenter.activity.ViewFullScreenActivityPresenter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.toolBar
import kotlinx.android.synthetic.main.activity_show_img_full_screen.ImgSelectFullScreen
import kotlinx.android.synthetic.main.activity_show_img_full_screen.view.btnBackChatActivity
import org.jetbrains.annotations.NotNull

@AndroidEntryPoint
class ShowImgFullScreenActivity : AppCompatActivity(), OnClickListener,
    ViewFullScreenContract.view {

    private val presenter by lazy { ViewFullScreenActivityPresenter(this, this) }
    private var isVisibilytyToolBar = true

    private val REQUEST_CODE_DOWLOAD_IMG: Int = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_img_full_screen)
        initUi()
        getBundleData()
        setEventImgClickListener()
    }

    private fun initUi() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = ""
        hideNavBar()
        registerEventToolBar()
    }

    private fun registerEventToolBar() {
        toolBar.btnBackChatActivity.setOnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_full_screen_activity, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setEventImgClickListener() {
        ImgSelectFullScreen.setOnClickListener(this)

        ImgSelectFullScreen.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {

                if (ImgSelectFullScreen.isZoomed) {

                    if (isVisibilytyToolBar == true) {
                        isVisibilytyToolBar = false
                        toolBar.visibility = View.INVISIBLE
                    }

                }

                return true
            }
        })
    }

    private fun getBundleData() {
        val imgUrl = intent.getStringExtra("urlImg")
        Glide.with(applicationContext)
            .asBitmap()
            .load(imgUrl)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    ImgSelectFullScreen.setImageBitmap(resource)
                }
            })
        //Glide.with(this).asBitmap().load(imgUrl).into(ImgSelectFullScreen)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_download_image -> {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    presenter.saveImage(ImgSelectFullScreen)
                } else {
                    askPermission()
                }
            }

            R.id.menu_item_nav_viewFullt -> {

            }

        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NotNull permissions: Array<String?>,
        @NotNull grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_DOWLOAD_IMG) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.saveImage(ImgSelectFullScreen)
            } else {
                Toast.makeText(this, "Please provide the required permissions", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun askPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_DOWLOAD_IMG
        )
    }


    private fun hideNavBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            toolBar.btnBackChatActivity.id -> {
                finish()
            }

            R.id.ImgSelectFullScreen -> {
                handleClickHideToolBar()
            }

        }
    }

    private fun handleClickHideToolBar() {
        if(ImgSelectFullScreen.isZoomed)return

        if (isVisibilytyToolBar == true) {
            isVisibilytyToolBar = false
            toolBar.visibility = View.INVISIBLE
        } else {
            isVisibilytyToolBar = true
            toolBar.visibility = View.VISIBLE
        }
    }

}