package com.mits.subscription.data.repo

import android.net.Uri

data class PaymentFile(val name:String,  val uri: Uri, private val mimeType:String){
    fun isPicture() = mimeType.startsWith("image/")
    fun isForExternalUse() = mimeType == "application/pdf"
}