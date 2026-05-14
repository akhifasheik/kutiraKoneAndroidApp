package com.kutira.kone.ui.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val ROLE = "role"
    const val AUTH = "auth"

    const val VENDOR_HOME = "vendor_home"
    const val VENDOR_UPLOAD = "vendor_upload"

    const val CUSTOMER_HOME = "customer_home"

    const val FABRIC_DETAIL_PATTERN = "fabric_detail/{fabricId}"
    fun fabricDetail(fabricId: String) = "fabric_detail/$fabricId"

    // ADD THESE
    const val CART_PATTERN = "cart/{userId}"
    fun cart(userId: String) = "cart/$userId"

    const val CHECKOUT = "checkout"

    const val SHIPPED_PRODUCTS = "shipped_products"

    const val ORDERS = "orders"

    const val VENDOR_ORDERS = "vendor_orders"
}