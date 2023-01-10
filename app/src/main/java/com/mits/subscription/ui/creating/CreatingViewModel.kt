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
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    val folders:LiveData<List<Folder>> = repository.subsFolders
    private val viewModelState = mutableStateOf(CreatingState())

    fun init(){
        viewModelState.value = CreatingState()
    }

    val uiState = viewModelState
    fun create(subscription: Subscription) {
        val copy = CreatingState()
        copy.isLoading = true
        viewModelState.value= copy
        viewModelScope.launch {
            repository.createSubscription(subscription)
            val copy2 = CreatingState()
            copy2.isLoading = false
            copy2.finished = true

            viewModelState.value= copy2
        }
    }

    fun checkName(name: String) {
        if (name.isBlank()) {
            viewModelState.value.nameError =  R.string.name_error
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

    class CreatingState {
        var nameError: Int? = null
        var savingAvailable: Boolean = false
        var finished: Boolean = false
        var generalError: Int? = null
        var isLoading: Boolean = false
    }
}