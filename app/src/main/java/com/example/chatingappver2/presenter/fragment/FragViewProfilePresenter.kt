package com.example.chatingappver2.presenter.fragment

import com.example.chatingappver2.contract.fragment.FragViewProfileContract
import com.example.chatingappver2.contract.repository.ProfileRepositoryContract
import com.example.chatingappver2.model.UserProfile

class FragViewProfilePresenter(
    private val view: FragViewProfileContract.View,
    private val repository: ProfileRepositoryContract
) : FragViewProfileContract.Presenter , ProfileRepositoryContract.GetProfileFinishListener {

    override fun getInformation() {
     repository.loadHeaderProfile(this)
    }

    override fun loadSuccess(result: UserProfile) {
        view.setInformation(result)
    }

    override fun loadError(message: String) {
        view.getInforError(message)
    }


}