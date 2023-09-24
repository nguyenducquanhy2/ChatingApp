package com.example.chatingappver2.presenter.activity

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.widget.DatePicker
import com.example.chatingappver2.contract.activity.CreateProfileContract
import com.example.chatingappver2.contract.repository.ProfileRepositoryContract
import com.example.chatingappver2.database.repository.ProfileRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class PresenterCreateProfile(
    private var view: CreateProfileContract.view,
    private val context: Context,
    private val repository: ProfileRepository
) :
    CreateProfileContract.presenter, ProfileRepositoryContract.SubmitProfileFinishListener {
    private val TAG: String? = "PresenterCreateProfile"
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val database = FirebaseDatabase.getInstance()
    private val reference = database.reference
    private val storage = FirebaseStorage.getInstance().reference
    private var calendar = Calendar.getInstance()

    override fun submitProfileToFireBase(uri: Uri, fullname: String, dateChoose: String) {
        if (fullname.isEmpty()) {
            view.fullNameEmpty()
            return
        }

        if (validDate(dateChoose) == true) {
            view.dateInValid()
            return
        }

        repository.submitProfile(uri, fullname, dateChoose,this)
    }

    private fun validDate(date: String): Boolean {
        return date.trim() == "yyyy/mm/dd"
    }

    override fun openDialogDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DATE)

        DatePickerDialog(context, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(View: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val formater = SimpleDateFormat("yyyy-MM-dd")
                calendar.set(year, month, dayOfMonth)
                val DateCheckIn: String = formater.format(calendar.time)
                view.setTextViewDate(DateCheckIn)
            }
        }, year, month, day).show()
    }

    override fun submitSuccess() {
        view.submitSuccess()
    }

    override fun submitError(message: String) {
        view.submitFail()
    }


}