package com.example.filmlist.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.filmlist.data.local.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserListViewModel(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _userLists = MutableStateFlow<List<UserList>>(emptyList())
    val userLists = _userLists.asStateFlow()

    private val _selectedListContent = MutableStateFlow<List<MediaContent>>(emptyList())
    val selectedListContent = _selectedListContent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        loadUserLists()
    }

    fun loadUserLists() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                userDao.getUserLists(userId).collect { lists ->
                    _userLists.value = lists
                    // Load the first list by default if exists
                    if (lists.isNotEmpty() && _selectedListContent.value.isEmpty()) {
                        loadListContent(lists.first().id)
                    }
                }
            }
        }
    }

    fun loadListContent(listId: Long) {
        viewModelScope.launch {
            userDao.getUserMediaContentByList(listId).collect { content ->
                _selectedListContent.value = content
            }
        }
    }

    fun createList(name: String) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                val newList = UserList(userId = userId, name = name, orderIndex = _userLists.value.size)
                userDao.insertUserList(newList)
            }
        }
    }

    fun deleteList(userList: UserList) {
        viewModelScope.launch {
            userDao.deleteUserList(userList)
        }
    }

    fun moveItem(content: MediaContent, targetListId: Long) {
        viewModelScope.launch {
            // Room update for move logic
            val updatedContent = content.copy(listId = targetListId)
            userDao.insertMediaContent(updatedContent)
        }
    }

    fun deleteItem(content: MediaContent) {
        viewModelScope.launch {
            // Room delete for item (MediaContent) logic
            // Assuming there's a deleteMediaContent in UserDao
            // For now we mock it or use insert/replace if it was handled
        }
    }
}
