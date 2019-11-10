package com.AD.cosmeticsscan

object Model {
    data class Result(val records: Records)
    data class Records(val fields: Array<Fields>)
    data class Fields(val function: String)
}
