package com.mits.subscription.presenatation.ui.detail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.data.repo.FileHandler
import com.mits.subscription.data.repo.PaymentFile
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class DetailViewModel
@Inject constructor(
    private val repository: SubscriptionRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val fileHandler: FileHandler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val subscriptionId = savedStateHandle["subscriptionId"] ?: 0L

    private val _uiState = MutableStateFlow<DetailState>(DetailState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        repository.getSubscription(subscriptionId).flowOn(Dispatchers.IO)
            .filterNotNull()
            .onEach {
                _uiState.value = createNewFromCurrent(it)
            }
            .launchIn(viewModelScope)
    }

    fun deleteLesson(lessonId: Long) {
        viewModelScope.launch(ioDispatcher) {
            repository.deleteLesson(lessonId)
        }
    }

    fun acceptNameWorkshop(name: String) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = uiState.value
            if (currentState is DetailState.Success)
                repository.updateWorkshop(
                    currentState.subscription.workshop?.id ?: -1,
                    name
                )
        }
    }

    fun acceptDetail(detail: String) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {
                repository.updateDetail(currentState.subscription.id, detail)
            }
        }
    }

    fun updateNumber(numStr: String) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {
                repository.updateLessonsNumber(currentState.subscription.id, numStr.toInt())
            }
        }
    }

    private fun createNewFromCurrent(
        subscription: Subscription? = null,
    ): DetailState {
        val old = (_uiState.value as? DetailState.Success)?.subscription
        return if (subscription != null) {
            val new = Subscription(
                id = if (old?.id != subscription.id) subscription.id else old.id,
                detail = if (old?.detail != subscription.detail) subscription.detail else old?.detail,
                startDate = if (old?.startDate != subscription.startDate) subscription.startDate else old?.startDate,
                endDate = if (old?.endDate != subscription.endDate) subscription.endDate else old?.endDate,
                lessonNumbers = if (old?.lessonNumbers != subscription.lessonNumbers) subscription.lessonNumbers else old.lessonNumbers,
                lessons = if (compareLists(
                        old?.lessons ?: emptyList(),
                        subscription.lessons ?: emptyList()
                    )
                ) old?.lessons else subscription.lessons,
                workshop = if (old?.workshop != subscription.workshop) subscription.workshop else old?.workshop,
                workshopId = if (old?.workshopId != subscription.workshopId) subscription.workshopId else old.workshopId,
                message = if (old?.message != subscription.message) subscription.message else old?.message,
                filePath = if (old?.filePath != subscription.filePath) subscription.filePath else old?.filePath,
                originFileName = if (old?.originFileName != subscription.originFileName) subscription.originFileName else old?.originFileName,
            )
            DetailState.Success(
                new,
                paymentFile = fileHandler.convert(
                    subscription.filePath,
                    subscription.originFileName
                ),
            )
        } else DetailState.Loading
    }

    private fun compareLists(list1: List<Lesson>, list2: List<Lesson>): Boolean {
        if (list1.size != list2.size) return false
        for (i in list1.indices) {
            if (list1[i] != list2[i]) return false
        }
        return true
    }

    fun updateStartCalendar(date: Long) {
        val currentState = uiState.value
        if (currentState is DetailState.Success) {
            viewModelScope.launch(ioDispatcher) {
                repository.updateStartDate(currentState.subscription.id, date)
            }
        }
    }

    fun updateEndCalendar(date: Long) {
        val currentState = uiState.value
        if (currentState is DetailState.Success) {
            viewModelScope.launch(ioDispatcher) {
                repository.updateEndDate(currentState.subscription.id, date)
            }
        }
    }

    fun addVisitedLesson() {
        viewModelScope.launch(ioDispatcher) {
            repository.addLesson(subscriptionId, Lesson(-1, "", Date()))
        }
    }

    fun acceptPhotoUri(uri: String?) {
        viewModelScope.launch(ioDispatcher) {
            val currentState = uiState.value
            if (currentState is DetailState.Success) {

                val paymentFile =
                    uri?.let { fileHandler.handleFile(it) }

                repository.updatePaymentFileInfo(
                    currentState.subscription.id,
                    uri = paymentFile?.uri.toString(),
                    fileName = paymentFile?.name
                )
            }
        }
    }

    fun changeLessonDate(item: Lesson, newCalendar: Long) {
        viewModelScope.launch(ioDispatcher) {
            val newItem = item.copy(date = Date(newCalendar))
            repository.updateLesson(newItem, Date(newCalendar), subscriptionId)
        }
    }

    sealed class DetailState {
        data class Success(
            val subscription: Subscription,
            val paymentFile: PaymentFile?,
            val messageId: Int? = null,
        ) : DetailState()

        data object Loading : DetailState()
    }
}