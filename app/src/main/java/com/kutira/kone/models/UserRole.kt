package com.kutira.kone.models

enum class UserRole {
    VENDOR,
    CUSTOMER;

    companion object {
        fun fromRaw(value: String?): UserRole? = when (value) {
            VENDOR.name -> VENDOR
            CUSTOMER.name -> CUSTOMER
            else -> null
        }
    }
}
