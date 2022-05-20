package com.mits.subscription.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import java.util.*


data class Lesson(
    var lId: Long,
    var description: String,
    var date:Date,

   /* @ColumnInfo(name = "subscription_id")
    var subscription_id: Long*/
)