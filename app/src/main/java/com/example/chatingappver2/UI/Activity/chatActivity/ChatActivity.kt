package com.example.chatingappver2.UI.Activity.chatActivity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.net.Uri
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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapplication.Ui.InterfaceAdapter.MessageOnClick
import com.example.chatingappver2.Model.Message
import com.example.chatingappver2.Model.UserProfile
import com.example.chatingappver2.R
import com.example.chatingappver2.UI.Activity.CallingActivity
import com.example.chatingappver2.UI.Activity.MainActivity.HomeActivity
import com.example.chatingappver2.UI.Activity.ShowFullScreenImg.ShowImgFullScreenActivity
import com.example.chatingappver2.UI.Activity.VideoCallActivity
import com.example.chatingappver2.UI.Activity.viewInforActivity
import com.example.chatingappver2.UI.Adapter.MessageAdapter
import com.example.chatingappver2.UI.Dialog.moreDialogMessage
import com.example.finalprojectchatapplycation.Dialog.progressDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import kotlinx.coroutines.launch
import java.util.*


class ChatActivity : AppCompatActivity(), OnClickListener, ChatActivityContract.view,
    MessageOnClick {

    private val TAG: String = "ChatActivity"
    private val REQUEST_CODE_OPEN_GALLERY: Int = 111
    private val REQUEST_CODE_OPEN_CAMERA: Int = 666
    private val presenter by lazy { ChatActivityPresenter(this, this) }
    private lateinit var adapterMsg: MessageAdapter
    private var messages: MutableList<Message> = mutableListOf()
    private var accountFocus: UserProfile? = null

    private lateinit var uriImg: Uri
    private lateinit var dialog: Dialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        dialog = progressDialog.progressDialog(this)
        dialog.show()
        getDataAndSetValueOnActivity()

    }

    private fun getDataAndSetValueOnActivity() {
        initUi()
        getBundleFromIntent()
        initRecyclerMessage()
        presenter.getMessages()
        dialog.dismiss()
    }

    private fun initRecyclerMessage() {
        val linearLayoutManager = LinearLayoutManager(this@ChatActivity)
        adapterMsg = MessageAdapter(messages, this@ChatActivity, this)
        recyclerChat.layoutManager = linearLayoutManager
        recyclerChat.adapter = adapterMsg
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
        val idUserReceive = intent.getStringExtra("idUserReceive")
        presenter.setAcountForcus(idUserReceive!!)

    }

    private fun registerClickListenerToolBar() {
        toolBar.imgInfor.setOnClickListener(this)
        toolBar.btnBackActivity.setOnClickListener(this)
        toolBar.btnVoiceCall.setOnClickListener (this)
        toolBar.btnVideoCall.setOnClickListener (this)
        toolBar.btnVideoCall.setOnClickListener(this)
    }

    private fun hideNavBar() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
            R.id.imgInfor -> {
                val intent = Intent(this, viewInforActivity::class.java)
                val bundle = Bundle()
                bundle.putSerializable("accountForcus", accountFocus)
                intent.putExtra("dataFromChatActivity", bundle)
                startActivity(intent)
            }
            R.id.btnVoiceCall -> {
                if (HomeActivity.client!!.isConnected==false)return
                val idUser= accountFocus?.idUser
                val intent=Intent(this, CallingActivity::class.java)
                intent.putExtra("isComingCall",false)
                intent.putExtra("to",idUser)
                startActivity(intent)
            }

            R.id.btnVideoCall -> {
                if (HomeActivity.client!!.isConnected==false)return
                val idUser=accountFocus?.idUser
                val intent=Intent(this, VideoCallActivity::class.java)
                intent.putExtra("isComingCall",false)
                intent.putExtra("to",idUser)
                startActivity(intent)
            }

        }
    }

    private fun takePhoto() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture" + Date().time.toString())
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera")
        uriImg = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImg)
        startActivityForResult(intent, REQUEST_CODE_OPEN_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CODE_OPEN_GALLERY
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_OPEN_GALLERY && resultCode == RESULT_OK && data != null) {
            dialog.show()
            uriImg = data.data!!
            presenter.sendImg(uriImg)
        }

        if (requestCode == REQUEST_CODE_OPEN_CAMERA && resultCode == RESULT_OK) {
            dialog.show()
            presenter.getImageUriAndSending(contentResolver, uriImg)
        }
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
            messages.add(message)
            adapterMsg.notifyDataSetChanged()
            recyclerChat.smoothScrollToPosition(messages.size - 1)
        }
    }



    override fun removeMessageForEveryone() {
        CoroutineScope(Dispatchers.Main).launch {

        }

    }

    override fun sendImgSuccess() {
        CoroutineScope(Dispatchers.Main).launch { dialog.dismiss() }
    }

    override fun sendImgFail() {
        CoroutineScope(Dispatchers.Main).launch { dialog.dismiss() }
    }

    override fun getCurrentMessages(): MutableList<Message> {

        return messages
    }

    override fun removeMsgForMe(count: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            messages.removeAt(count)
            adapterMsg.notifyDataSetChanged()
        }
    }

    override fun messageValueChange(messageChange: Message, index: Int) {
        messages[index]=messageChange
        adapterMsg.notifyItemChanged(index)
    }

    override fun msgOnLongClickListener(
        view: View,
        message: String,
        urlImage: String,
        keyMsg: String
    ) {
        val msgSenderRemove="You unsent a message"
        val msgReceiverRemove="${accountFocus?.fullname} unsent a message"

        if (message==msgSenderRemove||message==msgReceiverRemove){
            openDialogRemovedMesage(keyMsg)
        }else{
            openDialogForMesageText(view,urlImage,keyMsg)
        }
    }

    private fun openDialogRemovedMesage( keyMsg: String) {
        val view =LayoutInflater.from(this).inflate(R.layout.layout_delete_for_you_message_removed,null)
        val dialog=BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.removeForYouDialogMsgRemoved.setOnClickListener {

            presenter.unsendMsgForYou(keyMsg)
            dialog.dismiss()
        }
        dialog.show()
    }


    override fun ImageMessageOnClickListener(urlImage: String) {
        changeActivityFullViewImg(urlImage)
    }

    private fun changeActivityFullViewImg(urlImage: String) {
        val intent=Intent(this,ShowImgFullScreenActivity::class.java)
        intent.putExtra("urlImg",urlImage)
        startActivity(intent)
    }

    private fun openDialogForMesageText(viewLongfocus: View, urlImage: String,keyMsg:String) {

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
            showMoreLayout(keyMsg)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showMoreLayout(keyMsg:String) {
        val dialogMoreMsg=moreDialogMessage.moreDialogMessage(this)
        val btnForward=dialogMoreMsg.forwardMessage
        val btnRemove=dialogMoreMsg.removeMessage
        btnForward.setOnClickListener {
            Toast.makeText(this, "Forward", Toast.LENGTH_SHORT).show()
            dialogMoreMsg.dismiss()
        }

        btnRemove.setOnClickListener {
            showDialogRemoveMsg(keyMsg)
            dialogMoreMsg.dismiss()
        }

        dialogMoreMsg.show()
    }

    private fun showDialogRemoveMsg(keyMsg:String) {
        val view = LayoutInflater.from(this).inflate(R.layout.layout_delete_message, null)
        val dialog = BottomSheetDialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(view)

        dialog.unsendEveryOne.setOnClickListener {
            Toast.makeText(this, "unsend", Toast.LENGTH_SHORT).show()
            presenter.unsendMsgForEveryOne(keyMsg)
            dialog.dismiss()
        }

        dialog.removeForYou.setOnClickListener {
            presenter.unsendMsgForYou(keyMsg)
            dialog.dismiss()
        }

        dialog.show()
    }

}