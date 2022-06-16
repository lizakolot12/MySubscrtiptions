package com.mits.subscription.ui.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Lesson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateFolderViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) :
    ViewModel() {
        fun createFolder(name:String){
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    repository.createFolder(name)
                }

            }
        }
    }