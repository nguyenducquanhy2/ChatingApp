package com.example.chatingappver2.presenter.activity

import android.util.Log
import com.example.chatingappver2.model.UserProfile
import com.example.chatingappver2.contract.activity.MainActivityContract
import com.example.chatingappver2.contract.repository.ProfileRepositoryContract
import com.example.chatingappver2.database.repository.ProfileRepository

class MainActivityPresenter(
    private val view: MainActivityContract.view,
    private val repository: ProfileRepository
) :
    MainActivityContract.presenter, ProfileRepositoryContract.GetProfileFinishListener {
    private val TAG: String = "MainActivityPresenter"

    private fun notifyOnlineOrOfflineForEveryone(isOnline: Boolean) {
    repository.notifyOnlineOrOfflineForEveryone(isOnline)
    }

    override fun logout() {
        notifyOffline()
        view.SignOutSuccess()
    }

    private fun notifyOffline() {
        notifyOnLineOrOfflineOnProfile(false)
        notifyOnlineOrOfflineForEveryone(false)
    }

    private fun notifyOnLineOrOfflineOnProfile(isOnline: Boolean) {
        repository.notifyOnLineOrOfflineOnProfile(isOnline)
    }

    override fun loadHeaderProfile() {
        repository.loadHeaderProfile(this)
    }

    override fun setNotifityOnline() {
        notifyOnLineOrOfflineOnProfile(true)
        notifyOnlineOrOfflineForEveryone(true)
    }


    override fun loadSuccess(result: UserProfile) {
        view.setHeaderProfile(result)
    }

    override fun loadError(message: String) {
        Log.e(TAG, "loadError: $message")
    }
}