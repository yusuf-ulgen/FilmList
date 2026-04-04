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

    private val _error = MutableSharedFlow<String>()
    val error = _error.asSharedFlow()

    init {
        loadUserLists()
    }

    private fun loadUserLists() {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null && userId != -1L) {
                userDao.getUserLists(userId).collect { lists ->
                    // Tüm İçerikler (Yorum Yapılanlar) listesini sanal olarak en başa ekleyelim
                    val allContentList = UserList(id = -1L, userId = userId, name = "Yorum Yapılanlar", orderIndex = -1)
                    val transformedLists = listOf(allContentList) + lists
                    _userLists.value = transformedLists
                }
            }
        }
    }

    fun loadListContent(listId: Long) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                if (listId == -1L) {
                    // Tüm içerikleri getir (Yorum yapılanlar listesi için)
                    userDao.getUserMediaContent(userId).collect { content ->
                        _selectedListContent.value = content
                    }
                } else {
                    // Sadece seçili listeye ait içerikleri getir
                    userDao.getUserMediaContentByList(listId).collect { content ->
                        _selectedListContent.value = content
                    }
                }
            }
        }
    }

    fun createList(name: String) {
        viewModelScope.launch {
            val userId = sessionManager.userId.first()
            if (userId != null) {
                val count = userDao.countUserListByName(userId, name)
                if (count > 0 || name == "Yorum Yapılanlar") {
                    _error.emit("Bu isimde bir liste zaten var.")
                    return@launch
                }
                
                val newList = UserList(userId = userId, name = name, orderIndex = _userLists.value.size)
                userDao.insertUserList(newList)
            }
        }
    }

    fun updateList(userList: UserList, newName: String) {
        if (userList.id == -1L) return // System list cannot be renamed
        viewModelScope.launch {
            val updatedList = userList.copy(name = newName)
            userDao.updateUserList(updatedList)
        }
    }

    fun deleteList(userList: UserList) {
        if (userList.id == -1L) return // System list cannot be deleted
        viewModelScope.launch {
            userDao.deleteUserList(userList)
        }
    }

    fun moveItem(content: MediaContent, targetListId: Long) {
        if (targetListId == -1L) return // Cannot move item to virtual system list
        viewModelScope.launch {
            val updatedContent = content.copy(listId = targetListId)
            userDao.insertMediaContent(updatedContent)
        }
    }

    fun deleteItem(content: MediaContent) {
        viewModelScope.launch {
            userDao.deleteMediaContent(content)
        }
    }
}
