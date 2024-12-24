package com.myjar.jarassignment.data.repository

import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface JarRepository {
    suspend fun fetchResults(): Flow<List<ComputerItem>>
    suspend fun fetchItemDetails(itemId: String): Flow<ComputerItem?>
}

class JarRepositoryImpl(
    private val apiService: ApiService
) : JarRepository {

    private var allItems: List<ComputerItem> = emptyList()

    override suspend fun fetchResults(): Flow<List<ComputerItem>> = flow {
        try {
            val items = apiService.fetchResults()
            allItems = items
            emit(items)
        } catch (e: Exception){
            println(e.message)
            emit(emptyList())
        }
    }

    override suspend fun fetchItemDetails(itemId: String): Flow<ComputerItem?> = flow {
        try {
            if (allItems.isEmpty()) {
                val items = apiService.fetchResults()
                allItems = items
            }
            val item = allItems.find { it.id == itemId }
            emit(item)
        } catch (e: Exception) {
            println("Error fetching item details: ${e.message}")
            emit(null)
        }
    }
}