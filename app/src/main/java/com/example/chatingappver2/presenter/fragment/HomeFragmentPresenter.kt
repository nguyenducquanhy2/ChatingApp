package com.example.chatingappver2.presenter.fragment

import com.example.chatingappver2.model.CurrentContacts
import com.example.chatingappver2.contract.fragment.HomeContract
import com.example.chatingappver2.contract.repository.AccountRepositoryContract

class HomeFragmentPresenter(
    private val view: HomeContract.View,
    private val repository: AccountRepositoryContract
) : HomeContract.Presenter,AccountRepositoryContract.GetContactFinishListener {

    override fun getCurrentContact() {
        repository.getCurrentContact(this)
    }

    override fun currentContactsAdd(currentContacts: CurrentContacts) {
        view.currentContactsAdd(currentContacts)
    }

    override fun currentContactsChange(currentContacts: CurrentContacts) {
        view.currentContactsChange(currentContacts)
    }

    override fun getCurrentError(message: String) {
        view.getCurrentError(message)
    }


}