package com.example.chatingappver2.UI.Fragment.ChangePassword

import com.example.chatapplication.Ui.Fragment.ChangePassword.ChangePasswordContract
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordPresenter(private val view: ChangePasswordContract.view) :
    ChangePasswordContract.presenter {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser


    public override fun changePass(oldPass: String, NewPass: String, ConfirmPass: String) {
        if (!validPassword(oldPass)) {
            view.OldPassInvalid()
        }
        if (!validPassword(NewPass)) {
            view.NewPassInvalid()
        }
        if (!EqualPassword(NewPass, ConfirmPass)) {
            view.NewPassNotEqual()
        }

        val email: String = currentUser?.email!!

        auth.signInWithEmailAndPassword(email, oldPass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                currentUser.updatePassword(NewPass)
                    .addOnCompleteListener { taskUpdatePass ->
                        if (taskUpdatePass.isSuccessful()) {
                            view.changePassSucces()
                        } else {
                            view.changePassFail()
                        }
                    }

            } else {
                view.OldPasswordNotContans()
            }
        }
    }

    fun EqualPassword(NewPassword: String?, ConfirmPassword: String?): Boolean {
        return (NewPassword?.trim ().toString() == ConfirmPassword?.trim ().toString())
    }

    fun validPassword(value: String): Boolean {
        return value.length >= 6
    }

}