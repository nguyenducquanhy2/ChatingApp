package com.example.chatingappver2.model

import java.io.Serializable

class CurrentContacts : UserProfile, Serializable {
    var idUserLastSentMsg: String = ""
    var lastSentDate: String = ""
    var lastSentMsg: String = ""
    var notifyNewMsg: Boolean? = null


    constructor() {}

    constructor(
        dateOfBirth: String,
        email: String,
        fullname: String,
        idUser: String,
        theyIsActive: Boolean?,
        urlImgProfile: String,
        idUserLastSentMsg: String,
        lastSentDate: String,
        lastSentMsg: String,
        notifyNewMsg: Boolean?
    ) : super(dateOfBirth, email, fullname, idUser, theyIsActive, urlImgProfile) {
        this.idUserLastSentMsg = idUserLastSentMsg
        this.lastSentDate = lastSentDate
        this.lastSentMsg = lastSentMsg
        this.notifyNewMsg = notifyNewMsg
    }


}