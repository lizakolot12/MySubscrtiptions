package com.mits.subscription.ui.creating.data

import com.mits.subscription.data.repo.PaymentFile
import com.mits.subscription.getDefaultDetail

data class CreatingState(
    val defaultDetailStrId: Int = getDefaultDetail(),
    var name: String = "",
    var detail: String = "",
    var number: Int = 0,
    val startDate: Long,
    val endDate: Long,
    val nameError: Int? = null,
    val savingAvailable: Boolean = false,
    val finished: Boolean = false,
    val generalError: Int? = null,
    val fileUri: PaymentFile? = null,
    var isLoading: Boolean = false
)

