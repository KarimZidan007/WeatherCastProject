//package com.example.mvvm_demo.model.datasources
//
//import android.util.Log
//import kotlinx.coroutines.flow.Flow
//
//class LocalDataSrcImplementation(private var localSrc: DAO) {
//
//     suspend fun getAllLocalProducts(): Flow<List<Product>>
//    {
//     return localSrc.getAllProducts()
//    }
//    suspend fun inserProduct(product: Product)
//    {
//        localSrc.insertProduct(product)
//    }
//    suspend fun deleteProduct(product: Product)
//    {
//        localSrc.delete(product)
//    }
//}