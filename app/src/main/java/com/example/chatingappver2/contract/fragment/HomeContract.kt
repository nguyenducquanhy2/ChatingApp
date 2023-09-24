package com.example.chatingappver2.contract.fragment

import com.example.chatingappver2.model.CurrentContacts

interface HomeContract {
    interface View{
        fun currentContactsAdd(currentContacts: CurrentContacts)
        fun currentContactsChange(currentContacts: CurrentContacts)
        fun getCurrentError(message: String)
    }

    interface Presenter{
        fun getCurrentContact()
    }

}