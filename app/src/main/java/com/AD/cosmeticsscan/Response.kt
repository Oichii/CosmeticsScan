package com.AD.cosmeticsscan
// classes used to describe format of JSON files used by database
// response from cosmetics database
data class Response (
    val nhits: Int,
    val parameters: Parameters,
    val records: Array<Records>,
    val record_timestamp:String)

data class Parameters(
    val dataset:String,
    val timezone:String,
    val q:String,
    val rows:Int,
    val format:String)

data class Records(
    val datasetid: String,
    val recordid:String,
    val fields:Fields)

data class Fields(
    val inci_name:String,
    val function:String,
    val update_date:String,
    val cosing_ref_no:Int,
    val chem_iupac_name_description:String,
    val ph_eur_name:String,
    val restriction:String,
    val cas_no:String,
    val ec_no:String,
    val inn_nam:String)

