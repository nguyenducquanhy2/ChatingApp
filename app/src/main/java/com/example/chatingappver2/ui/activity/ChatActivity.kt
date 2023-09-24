package com.example.chatingappver2.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatingappver2.model.Message
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.ui.adapter.MessageAdapter
import com.example.chatingappver2.ui.dialog.DialogMoreMessage
import com.example.chatingappver2.service.CallService
import com.example.chatingappver2.contract.activity.ChatActivityContract
import com.example.chatingappver2.presenter.activity.ChatActivityPresenter
import com.example.chatingappver2.ui.base.activity.BaseActivity
import com.example.chatingappver2.ui.dialog.DialogRemoveMessage
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_chat.view.*
import kotlinx.android.synthetic.main.bottom_shet_dialog.*
import kotlinx.android.synthetic.main.layout_delete_for_you_message_removed.removeForYouDialogMsgRemoved
import kotlinx.android.synthetic.main.layout_delete_message.removeForYou
import kotlinx.android.synthetic.main.layout_delete_message.unsendEveryOne
import kotlinx.android.synthetic.main.layout_more_message.forwardMessage
import kotlinx.android.synthetic.main.layout_more_message.removeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class ChatActivity : BaseActivity(), OnClickListener, ChatActivityContract.view {
    companion object {
        private const val REQUEST_CODE_OPEN_GALLERY: Int = 111
        private const val REQUEST_CODE_OPEN_CAMERA: Int = 666
    }

    private val TAG: String = "ChatActivity"
    private lateinit var job: Job
    private val presenter by lazy { ChatActivityPresenter(this, messageRepository) }
    private lateinit var adapterMsg: MessageAdapter

    private var accountFocus: UserProfile? = null

    private lateinit var uriImg: Uri

    override fun getLayoutID(): Int = R.layout.activity_chat

    override fun onCreateActivity() {
        showLoadingProgress()
        getDataAndSetValueOnActivity()
    }

    private fun getDataAndSetValueOnActivity() {
        getBundleFromIntent()
        initUi()
        initRecyclerMessage()
        dismissLoadingProgress()
    }

    private fun initRecyclerMessage() {
        createAdapter()
        val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
        recyclerChat.layoutManager = linearLayoutManager
        recyclerChat.adapter = adapterMsg
    }

    @SuppressLint("SuspiciousIndentation")
    private fun createAdapter() {
        adapterMsg = MessageAdapter()
        adapterMsg.msgOnLongClickListener = { view: View, message: String,
                                              urlImage: String, keyMsg: String,
                                              isMyMessage: Boolean ->
            msgOnLongClickListener(view, message, urlImage, keyMsg, isMyMessage)
        }

        adapterMsg.imageMessageOnClickListener = { urlImage ->
            imageMessageOnClickListener(urlImage)
        }

        adapterMsg.imageShowFullScreen = { urlImage ->
            val intent = Intent(this, ShowImgFullScreenActivity::class.java)
            intent.putExtra("urlImg", urlImage)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    private fun initUi() {
        setSupportActionBar(toolBar)
        supportActionBar?.title = ""
        hideNavBar()
        registerClickListeneActivity()
    }

    private fun registerClickListeneActivity() {
        registerClickListenerToolBar()
        btnSenMessage.setOnClickListener(this)
        btnOpenAttachment.setOnClickListener(this)
        btnOpenCamera.setOnClickListener(this)
    }

    private fun getBundleFromIntent() {
        if (intent == null) return
        val idUserReceive = intent.getStringExtra("idUserReceive")
        job = CoroutineScope(Dispatchers.Default).launch {
            presenter.setAcountForcus(idUserReceive!!)
        }
    }

    private fun registerClickListenerToolBar() {
        toolBar.ViewLayoutInfor.setOnClickListener(this)
        toolBar.btnBackActivity.setOnClickListener(this)
        toolBar.btnVoiceCall.setOnClickListener(this)
        toolBar.btnVideoCall.setOnClickListener(this)
        toolBar.btnVideoCall.setOnClickListener(this)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        //If the touch position is outside the EditText then we hide the keyboard
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm =
                                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }
        return ret
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnBackActivity -> {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

            R.id.btnSenMessage -> {
                val message = edtMessage.text.trim().toString()
                presenter.submitSendMsg(message, "")
            }

            R.id.btnOpenCamera -> {
                takePhoto()
            }

            R.id.btnOpenAttachment -> {
                openGallery()
            }

            R.id.ViewLayoutInfor -> {
                val intent = Intent(this, ViewInforActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("accountForcus", accountFocus)
                intent.putExtra("dataFromChatActivity", bundle)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            R.id.btnVoiceCall -> {
                if (CallService.client!!.isConnected == false) return
                val idUser = accountFocus?.idUser
                val intent = Intent(this, VoiceCallActivity::class.java)
                intent.putExtra("isComingCall", false)
                intent.putExtra("to", idUser)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }

            R.id.btnVideoCall -> {
                if (CallService.client!!.isConnected == false) return
                val idUser = accountFocus?.idUser
                val intent = Intent(this, VideoCallActivity::class.java)
                intent.putExtra("isComingCall", false)
                intent.putExtra("to", idUser)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun checkPermissionRequest() {
        val permissionsNeeded = mutableListOf<String>()

        val allPermissions = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            allPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        allPermissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsNeeded.add(permission)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 0)
            return
        }

        openCamera()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isGranted = false
        if (grantResults.isNotEmpty()) {
            for (item in grantResults) {
                if (item != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false
                    break
                } else {
                    isGranted = true
                }

            }
        }

        if (requestCode == 0) {
            if (!isGranted) {
                finish()
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            } else {
                openCamera()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture" + Date().time.toString())
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        uriImg = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImg)
        startActivityForResult(intent, REQUEST_CODE_OPEN_CAMERA)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun takePhoto() {
        checkPermissionRequest()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CODE_OPEN_GALLERY
        )
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_GALLERY && resultCode == RESULT_OK && data != null) {
            dismissLoadingProgress()
            uriImg = data.data!!
            presenter.sendImg(uriImg)
        }

        if (requestCode == REQUEST_CODE_OPEN_CAMERA && resultCode == RESULT_OK) {
            dismissLoadingProgress()
            presenter.getImageUriAndSending(contentResolver, uriImg)
        }
    }

    override fun onNetworkConnected() {
        Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show()
    }

    override fun onNetworkDisconnected() {
        Toast.makeText(this, "Network not connected", Toast.LENGTH_SHORT).show()
    }

    override fun setInformationAccount(accountFocus: UserProfile?) {

        if (this.accountFocus == null) {
            this.accountFocus = accountFocus
            Glide.with(this).asBitmap().load(accountFocus?.urlImgProfile).into(toolBar.imgInfor)
            setUserOfActive()

        } else {
            this.accountFocus = accountFocus
            setUserOfActive()
        }

    }

    private fun setUserOfActive() {
        toolBar.tvFullNameChatActivity.text = accountFocus?.fullname
        if (accountFocus?.theyIsActive == true) {
            toolBar.tvActiveChatActivity.text = "Online"
        } else {
            toolBar.tvActiveChatActivity.text = "Offline"
        }
    }

    override fun sendMsgFail() {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(this@ChatActivity, "send message fail", Toast.LENGTH_SHORT).show()
        }

    }

    override fun sendMsgSuccess() {
        CoroutineScope(Dispatchers.Main).launch { edtMessage.text.clear() }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun addMessage(message: Message) {
        CoroutineScope(Dispatchers.Main).launch {
            adapterMsg.addItemToListUser(message)
            recyclerChat.smoothScrollToPosition(adapterMsg.getDefaultList().lastIndex)
        }
    }

    override fun removeMessageForEveryone() {
        CoroutineScope(Dispatchers.Main).launch {

        }

    }

    override fun sendImgSuccess() {
        CoroutineScope(Dispatchers.Main).launch { dismissLoadingProgress() }
    }

    override fun sendImgFail() {
        CoroutineScope(Dispatchers.Main).launch { dismissLoadingProgress() }
    }

    override fun getCurrentMessages(): MutableList<Message> {
        return adapterMsg.getDefaultList()
    }

    override fun removeMsgForMe(count: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            adapterMsg.removeItemAtIndex(count)
        }
    }

    override fun messageValueChange(messageChange: Message, index: Int) {
        adapterMsg.valueItemChange(messageChange, index)
    }

    private fun msgOnLongClickListener(
        view: View,
        message: String,
        urlImage: String,
        keyMsg: String,
        isMyMessage: Boolean
    ) {
        val msgSenderRemove = "You unsent a message"
        val msgReceiverRemove = "${accountFocus?.fullname} unsent a message"

        if (message == msgSenderRemove || message == msgReceiverRemove) {
            openDialogRemovedMesage(keyMsg)
        } else {
            openDialogForMesageText(view, urlImage, keyMsg, isMyMessage)
        }
    }

    private fun openDialogRemovedMesage(keyMsg: String) {
        val view =
            LayoutInflater.from(this).inflate(R.layout.layout_delete_for_you_message_removed, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.removeForYouDialogMsgRemoved.setOnClickListener {

            presenter.unsendMsgForYou(keyMsg)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun imageMessageOnClickListener(urlImage: String) {
        changeActivityFullViewImg(urlImage)
    }

    private fun changeActivityFullViewImg(urlImage: String) {
        val intent = Intent(this, ShowImgFullScreenActivity::class.java)
        intent.putExtra("urlImg", urlImage)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun openDialogForMesageText(
        viewLongfocus: View,
        urlImage: String,
        keyMsg: String,
        isMyMessage: Boolean
    ) {

        val view = LayoutInflater.from(this).inflate(R.layout.bottom_shet_dialog, null)
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(view)

        if (viewLongfocus is ImageView) {
            dialog.layOutContainCopy.visibility = View.GONE
            dialog.layOutContainDownloadImg.visibility = View.VISIBLE
        } else {
            dialog.layOutContainCopy.visibility = View.VISIBLE
            dialog.layOutContainDownloadImg.visibility = View.GONE
        }

        dialog.btnReply.setOnClickListener {
            Toast.makeText(this, "Reply", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.btnFullViewImage.setOnClickListener {
            changeActivityFullViewImg(urlImage)
            dialog.dismiss()
        }

        dialog.btnCopy.setOnClickListener {
            val textView = viewLongfocus as TextView
            val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("TextView", textView.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copy", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.btnPin.setOnClickListener {
            Toast.makeText(this, "Pin", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        dialog.btnMore.setOnClickListener {
            showMoreLayout(keyMsg, isMyMessage)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMoreLayout(keyMsg: String, isMyMessage: Boolean) {
        val dialogMoreMsg = DialogMoreMessage(this)
        dialogMoreMsg.showDialogRemoveMsg = {

            showDialogRemoveMsg(keyMsg, isMyMessage)
        }

        dialogMoreMsg.show()
    }

    private fun showDialogRemoveMsg(keyMsg: String, isMyMessage: Boolean) {

        val dialog = DialogRemoveMessage(this, isMyMessage)
        dialog.unsendMsgForEveryOne = {
            presenter.unsendMsgForEveryOne(keyMsg)
        }
        dialog.unsendMsgForYou = {
            presenter.unsendMsgForYou(keyMsg)
        }

        dialog.show()
    }

}