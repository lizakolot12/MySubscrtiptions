package com.mits.subscription.ui.list

import android.util.Log
import androidx.lifecycle.*
import com.mits.subscription.data.repo.SubscriptionRepository
import com.mits.subscription.model.Folder
import com.mits.subscription.model.Lesson
import com.mits.subscription.model.Subscription
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

data class ExpandableListItem(
    val folder: Folder,
    var expanded: Boolean = false
)

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) :
    ViewModel() {
    private val expandedIds: MutableSet<Long> = HashSet()
    private val _subsFolders: MediatorLiveData<List<ExpandableListItem>> = MediatorLiveData()
    val subsFolders: LiveData<List<ExpandableListItem>> = _subsFolders

    init {
        _subsFolders.addSource(repository.subsFolders) { newList ->
            run {
                updateSubFolders(transform(newList))
            }
        }

    }

    private fun transform(list: List<Folder>): List<ExpandableListItem> =
        list.map {
            ExpandableListItem(
                it,
                expandedIds.contains(it.id)
            )
        }

    fun changeExpand(expandableListItem: ExpandableListItem, expand: Boolean) {
        if (expand) {
            expandedIds.add(expandableListItem.folder.id)
            Log.e("TEST", "add expand"  + expandableListItem.folder.id)
        } else {
            expandedIds.remove(expandableListItem.folder.id)
            Log.e("TEST", "remove expand" + expandableListItem.folder.id)
        }
        val newList = _subsFolders.value?.map { it ->
            val isExpand =  expandedIds.contains(it.folder.id)
            Log.e("TEST", it.folder.name + "  " + it.folder.id + "  " + isExpand)
            ExpandableListItem(
                it.folder,
                isExpand
            )
        }
        newList?.let { updateSubFolders(it) }
    }

    @Synchronized
    private fun updateSubFolders(newList: List<ExpandableListItem>) {
        _subsFolders.value = newList
    }

    fun addVisitedLesson(subscription: Subscription) {
        viewModelScope.launch {
            repository.addLesson(subscription.id, Lesson(-1, "", Date()))
        }
    }

    fun delete(subscription: Subscription) {
        viewModelScope.launch {
            repository.deleteSubscription(subscription)
        }
    }

    fun copy(subscription: Subscription) {
        viewModelScope.launch {
            val newSubscription = Subscription(
                0, subscription.name + "_copy",
                Date(), Date(), subscription.lessonNumbers, subscription.description, emptyList()
            )
            repository.createSubscription(newSubscription)
        }
    }

    fun moveToFolder(folderId: Long, subscription: Subscription) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repository.addToFolder(folderId, subscription)
            }

        }

    }

    fun deleteFolder(folder: Folder) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                repository.deleteFolder(folder)
            }

        }
    }
}