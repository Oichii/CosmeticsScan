package com.AD.cosmeticsscan

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CosmeticsIngredientsService {
    @GET("/api/records/1.0/search/")
    fun checkIngredient(@Query("dataset") dataset: String,
                        @Query("q") q: String,
                        @Query("rows") rows: Int): Observable<Response>

}