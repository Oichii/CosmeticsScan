package com.AD.cosmeticsscan

// classes used to describe format of JSON files used by database
// response from database to save ingredients

data class Cosmetic_db(
    val id: Int,
    val name: String,
    val favourite: Boolean,
    val ingredients: Array<Int>
)

data class Cosmetic_save(
    val name: String,
    val favourite: Boolean,
    val ingredients: Array<Int>
)

data class Ingredient_db(
    val id: Int,
    val name: String,
    val function: String,
    val favourite: Boolean
)

data class Ingredient_save(
    val name: String,
    val function: String,
    val favourite: Boolean
)

