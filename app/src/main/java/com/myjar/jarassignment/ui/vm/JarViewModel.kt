package com.myjar.jarassignment.ui.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myjar.jarassignment.createRetrofit
import com.myjar.jarassignment.data.model.ComputerItem
import com.myjar.jarassignment.data.repository.JarRepository
import com.myjar.jarassignment.data.repository.JarRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JarViewModel : ViewModel() {

    private val _listStringData = MutableStateFlow<List<ComputerItem>>(emptyList())
    val listStringData: StateFlow<List<ComputerItem>>
        get() = _listStringData

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String>
        get() = _searchQuery

    private val _filteredList = MutableStateFlow<List<ComputerItem>>(emptyList())
    val filteredList: StateFlow<List<ComputerItem>>
        get() = _filteredList

    private val _itemDetails = MutableStateFlow<ComputerItem?>(null)
    val itemDetails: StateFlow<ComputerItem?>
        get() = _itemDetails

    private val repository: JarRepository = JarRepositoryImpl(createRetrofit())

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                repository.fetchResults()
                    .collect { items ->
                        _listStringData.value = items
                        _filteredList.value = items
                    }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val currentList = _listStringData.value
        _filteredList.value = if (query.isBlank()) {
            currentList
        } else {
            currentList.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun fetchItemDetails(itemId: String) {
        viewModelScope.launch {
            try {
                repository.fetchItemDetails(itemId).collect { details ->
                    _itemDetails.value = details
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}