package com.example.chatingappver2.model


class Message {
    var idSender: String = ""
    var keyMsg: String? = null
    var msgTxt: String = ""
    var time: String = ""
    var urlImg: String = ""

    constructor() { }

    constructor(idSender: String, keyMsg: String, msgTxt: String, time: String, urlImg: String) {
        this.idSender = idSender
        this.keyMsg = keyMsg
        this.msgTxt = msgTxt
        this.time = time
        this.urlImg = urlImg
    }

    constructor(idSender: String, msgTxt: String, time: String, urlImg: String) {
        this.idSender = idSender
        this.msgTxt = msgTxt
        this.time = time
        this.urlImg = urlImg
    }


}