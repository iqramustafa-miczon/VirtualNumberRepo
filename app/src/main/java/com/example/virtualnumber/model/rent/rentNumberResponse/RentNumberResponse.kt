package com.example.virtualnumber.model.rent.rentNumberResponse

data class RentNumberResponse(
    val status: String,
    val phone: PhoneDetails? = null,
    val message: String? = null,
    val errorMsg: String? = null
)