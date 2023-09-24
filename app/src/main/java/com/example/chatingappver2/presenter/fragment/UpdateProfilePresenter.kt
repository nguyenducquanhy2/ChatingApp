package com.example.chatingappver2.presenter.fragment

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.chatingappver2.contract.fragment.UpdateProfileContract
import com.example.chatingappver2.contract.repository.ProfileRepositoryContract
import com.example.chatingappver2.database.repository.ProfileRepository
import com.example.chatingappver2.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class UpdateProfilePresenter(
    private val view: UpdateProfileContract.View,
    private val context: Context,
    private val repository: ProfileRepository
) :
    UpdateProfileContract.Presenter,
    ProfileRepositoryContract.UpdateProfileFinishListener,
    ProfileRepositoryContract.GetProfileFinishListener {
    private val TAG: String = "UpdateProfilePresenter"

    override fun loadHeaderProfile() {
        repository.loadHeaderProfile(this)
    }


    override fun submitProfileToFireBase(uri: Uri?, fullname: String, dateChoose: String) {

        if (fullname.isNotEmpty() && !inValidDate(dateChoose) && (uri != null)) {
            updateThreePlace(uri, fullname, dateChoose)
            return
        }

        if (fullname.isNotEmpty() && !inValidDate(dateChoose)) {
            updateFullnameAndDate(fullname, dateChoose)
            return
        }

        if (!inValidDate(dateChoose) && (uri != null)) {
            updateImgAndDate(uri, dateChoose)
            return
        }

        if (fullname.isNotEmpty() && (uri != null)) {
            updateFullnameAndImg(fullname, uri)
            return
        }

        if (uri != null) {
            updateImg(uri)
            return
        }
        if (fullname.isNotEmpty()) {
            updateFullname(fullname)
            return
        }

        if (inValidDate(dateChoose)) {
            updateDate(dateChoose)
            return
        }

    }


    private fun updateDate(DateOfBirth: String) {

        CoroutineScope(IO).launch {
            updateMyProfileByPlace(DateOfBirth, "dateOfBirth")
        }

        CoroutineScope(IO).launch {
            updateProfileInCurrentContacts(DateOfBirth, "dateOfBirth")
        }
    }

    private fun updateFullname(fullname: String) {

        CoroutineScope(IO).launch {
            updateMyProfileByPlace(fullname, "fullname")
        }

        CoroutineScope(IO).launch {
            updateProfileInCurrentContacts(fullname, "fullname")

        }
    }

    private fun updateProfileInCurrentContacts(value: String, path: String) {
        repository.updateProfileInCurrentContacts(value, path, this)
    }

    private fun updateImg(uri: Uri) {
        repository.updateImg(uri, this)
    }

    private fun updateMyProfileByPlace(value: String, path: String) {
        repository.updateMyProfileByPlace(value, path, this)
    }

    private fun updateImgAndDate(uri: Uri, dateChoose: String) {
        updateImg(uri)
        updateDate(dateChoose)
    }

    private fun updateFullnameAndImg(fullname: String, uri: Uri) {
        updateImg(uri)
        updateFullname(fullname)
    }

    private fun updateFullnameAndDate(fullname: String, dateChoose: String) {
        updateFullname(fullname)
        updateDate(dateChoose)
    }

    private fun updateThreePlace(uri: Uri, fullname: String, dateChoose: String) {
        updateImg(uri)
        updateFullname(fullname)
        updateDate(dateChoose)
    }

    private fun inValidDate(value: String): Boolean {
        return value.trim() == "yyyy/mm/dd"
    }

    override fun onSuccess() {
        view.submitSuccess()
    }

    override fun loadSuccess(result: UserProfile) {
        view.setHeaderProfile(result)
    }

    override fun loadError(message: String) {
        Log.e(TAG, "loadError: $message")
    }

}
