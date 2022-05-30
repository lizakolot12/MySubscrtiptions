package com.mits.subscription.ui.creating

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.mits.subscription.R
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreatingViewModel
@Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    /*  private val _uiState = mutableStateOf(CreatingState())
      val uiState: State<CreatingState>
          get() = _uiState
  */
    private val viewModelState = mutableStateOf(CreatingState())

    fun init(){
        viewModelState.value = CreatingState()
    }

    val uiState = viewModelState
 /*       .map { it }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )*/

    fun create(subscription: Subscription) {
       // viewModelState.update { it.copy(isLoading = true) }
        val copy = CreatingState()
        copy.isLoading = true
        viewModelState.value= copy
        Log.e("TEST", "is loading must be true")
        viewModelScope.launch {
            repository.createSubscription(subscription)
            delay(3000)
            Log.e("TEST", "changing")
            val copy2 = CreatingState()
            copy2.isLoading = false
            copy2.finished = true

            viewModelState.value= copy2
           /* viewModelState.update{ it ->
                it.finished = true
                it.isLoading = false
                it
            }*/
        }
    }

    fun checkName(name: String) {
        if (name.isBlank()) {
            viewModelState.value.nameError =  R.string.name_error
          //  _uiState.value.nameError = R.string.name_error
        } else {
            viewModelState.value.nameError = null
           // _uiState.value.nameError = null
        }
        checkSaveAvailability()
    }

    fun checkStartDate(calendar: Calendar) {
        //_uiState.value.beginRestrictionEndDate = calendar
    }

    private fun checkSaveAvailability() {
    /*    _uiState.value.savingAvailable =
            _uiState.value.nameError == null &&
                    _uiState.value.generalError == null
                    && !_uiState.value.isLoading*/
        viewModelState.value.savingAvailable =
            viewModelState.value.nameError == null &&
                    viewModelState.value.generalError == null
                    && !viewModelState.value.isLoading
    }

    class CreatingState {
        var nameError: Int? = null
        var beginRestrictionEndDate: Calendar? = null
        var savingAvailable: Boolean = false
        var finished: Boolean = false
        var generalError: Int? = null
        var isLoading: Boolean = false
    }
}