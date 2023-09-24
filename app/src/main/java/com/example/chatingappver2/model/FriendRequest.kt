package com.example.chatingappver2.model


class FriendRequest {
    var accept: Boolean? = null
    var idUserReceiveRequest: String= ""
    var idUserSendRequest: String= ""
    var nameUserSendRequest: String= ""
    var urlAvatarUserSendRequest: String= ""


    constructor() {}

    constructor(
        accept: Boolean,
        idUserReceiveRequest: String,
        idUserSendRequest: String,
        nameUserSendRequest: String,
        urlAvatarUserSendRequest: String
    ) {
        this.accept = accept
        this.idUserReceiveRequest = idUserReceiveRequest
        this.idUserSendRequest = idUserSendRequest
        this.nameUserSendRequest = nameUserSendRequest
        this.urlAvatarUserSendRequest = urlAvatarUserSendRequest
    }


}