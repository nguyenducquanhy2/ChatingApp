package com.example.chatingappver2.model

import java.io.Serializable

open class UserProfile : Serializable {
    var dateOfBirth: String = ""
    var email: String = ""
    var fullname: String = ""
    var idUser: String = ""
    var theyIsActive: Boolean? = null
    var urlImgProfile: String = ""

    constructor() {}

    constructor(fullname: String, urlImgProfile: String, dateOfBirth: String) {
        email = ""
        idUser = ""
        this.fullname = fullname
        this.urlImgProfile = urlImgProfile
        this.dateOfBirth = dateOfBirth
    }

    constructor(
        dateOfBirth: String,
        email: String,
        fullname: String,
        idUser: String,
        theyIsActive: Boolean?,
        urlImgProfile: String
    ) {
        this.dateOfBirth = dateOfBirth
        this.email = email
        this.fullname = fullname
        this.idUser = idUser
        this.theyIsActive = theyIsActive
        this.urlImgProfile = urlImgProfile
    }

    override fun toString(): String {
        return "UserProfile(dateOfBirth='$dateOfBirth', email='$email', fullname='$fullname', idUser='$idUser', theyIsActive=$theyIsActive, urlImgProfile='$urlImgProfile')"
    }


}