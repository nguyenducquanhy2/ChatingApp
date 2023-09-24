package com.example.chatingappver2.presenter.fragment

import com.example.chatingappver2.contract.fragment.ChangePasswordContract
import com.example.chatingappver2.contract.repository.AuthRepositoryContract

class ChangePasswordPresenter(
    private val view: ChangePasswordContract.View,
    private val repositoryContract: AuthRepositoryContract
) :
    ChangePasswordContract.Presenter, AuthRepositoryContract.ChangePassWordListener {


    override fun changePass(oldPass: String, NewPass: String, ConfirmPass: String) {
        if (!validPassword(oldPass)) {
            view.oldPassInvalid()
        }
        if (!validPassword(NewPass)) {
            view.newPassInvalid()
        }
        if (!EqualPassword(NewPass, ConfirmPass)) {
            view.newPassNotEqual()
        }

        repositoryContract.changePassword(oldPass, NewPass, this)

    }

    fun EqualPassword(NewPassword: String?, ConfirmPassword: String?): Boolean {
        return (NewPassword?.trim().toString() == ConfirmPassword?.trim().toString())
    }

    fun validPassword(value: String): Boolean {
        return value.length >= 6
    }

    override fun changePasswordSuccess() {
        view.changePassSucces()
    }

    override fun changePasswordError(message: String) {
        view.changePassFail(message)
    }

    override fun checkOldPass() {
        view.oldPasswordNotContans()
    }

}