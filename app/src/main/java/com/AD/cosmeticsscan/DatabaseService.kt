package com.AD.cosmeticsscan

import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

interface DatabaseService {
        @GET("/cosmetic/")
        fun getCosmetics(): Observable<List<Cosmetic_db>>
}

interface DatabasePOSTService {
        @Headers("content-type: application/json")
        @POST("/cosmetic/")
        fun postCosmetics(@Body cosmeticDb: Cosmetic_save): Observable<Cosmetic_save>
}

interface DatabaseIngredientService {
        @GET("/ingredient/")
        fun getIngredient(): Observable<List<Ingredient_db>>
}

interface DatabasePOSTIngredientService {
        @Headers("content-type: application/json")
        @POST("/ingredient/")
        fun postIngredient(@Body ingredientDb:Ingredient_db): Observable<Ingredient_db>
}
