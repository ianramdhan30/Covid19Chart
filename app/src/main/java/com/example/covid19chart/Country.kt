package com.example.covid19chart



data class AllNegara(
    val Global: Dunia,
    val Countries: List<Negara>
)

data class Dunia(
    val TotalConfirmed: String = "",
    val TotalRecovered: String = "",
    val TotalDeath: String = ""
)

data class Negara(
    val Country: String = "",
    val Date: String = "",
    val NewConfirmed: String = "",
    val TotalConfirmed: String = "",
    val NewDeath: String = "",
    val TotalDeath: String = "",
    val NewRecovered: String = "",
    val TotalRecovered: String = "",
    val CountryCode: String = ""
)

data class InfoNegara(
    val Death: String = "",
    val Confirmed: String = "",
    val Recovered: String = "",
    val Active: String = "",
    val Date: String = ""
)