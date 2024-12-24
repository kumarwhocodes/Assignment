package com.myjar.jarassignment.data.repository

import com.myjar.jarassignment.data.api.ApiService
import com.myjar.jarassignment.data.model.ComputerItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface JarRepository {
    suspend fun fetchResults(): Flow<List<ComputerItem>>
}

class JarRepositoryImpl(
    private val apiService: ApiService
) : JarRepository {
    override suspend fun fetchResults(): Flow<List<ComputerItem>> = flow {
        try {
            emit(apiService.fetchResults())
        } catch (e: Exception){
            println(e.message)
            emit(emptyList())
        }
    }
}