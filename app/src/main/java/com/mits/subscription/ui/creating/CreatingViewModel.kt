package com.mits.subscription.ui.creating

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val viewModelState = mutableStateOf(CreatingState())

    fun init() {
        val defaultTag = getDefaultTag()
        viewModelState.value = CreatingState(defaultTag)

    }

    private fun getDefaultTag(): Int? {
        val currentDate = Calendar.getInstance()
        return when (currentDate.get(Calendar.MONTH)) {
            0 -> R.string.january
            1 -> R.string.february
            2 -> R.string.march
            3 -> R.string.april
            4 -> R.string.may
            5 -> R.string.june
            6 -> R.string.july
            7 -> R.string.august
            8 -> R.string.september
            9 -> R.string.october
            10 -> R.string.november
            11 -> R.string.december
            else -> {
                null
            }
        }
    }

    val uiState = viewModelState
    fun create(
        name: String,
        tag: String? = null,
        lessonNumbers: Int,
        startDate: Date,
        endDate: Date
    ) {
        val copy = CreatingState()
        copy.isLoading = true
        viewModelState.value = copy
        viewModelScope.launch {
            val folderId = repository.createFolder(name)
            val newSubscription = Subscription(
                -1,
                name = tag,
                startDate = startDate,
                endDate = endDate,
                lessonNumbers = lessonNumbers,
                folderId = folderId
            )
            repository.createSubscription(newSubscription)
            val newState = CreatingState()
            newState.isLoading = false
            newState.finished = true
            viewModelState.value = newState
        }
    }

    fun checkName(name: String) {
        if (name.isBlank()) {
            viewModelState.value.nameError = R.string.name_error
        } else {
            viewModelState.value.nameError = null
        }
        checkSaveAvailability()
    }

    private fun checkSaveAvailability() {
        viewModelState.value.savingAvailable =
            viewModelState.value.nameError == null &&
                    viewModelState.value.generalError == null
                    && !viewModelState.value.isLoading
    }

    fun checkTag(text: String) {
        if (text.isNotBlank()) {
            val newState = viewModelState.value
            newState.defaultTagStrId = null
            viewModelState.value = newState
        }
    }

    class CreatingState(var defaultTagStrId: Int? = null) {
        var nameError: Int? = null
        var savingAvailable: Boolean = false
        var finished: Boolean = false
        var generalError: Int? = null
        var isLoading: Boolean = false
    }
}