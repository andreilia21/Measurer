package com.dewerro.measurer

/**
 * Константные значения
 */
object K {
    object SharedPreferences {
        const val FIREBASE_USER_DATA = "user_data"
        const val FIREBASE_EMAIL = "email"
        const val FIREBASE_PASSWORD = "password"
    }
    object Bundle {
        const val GALLERY_IMAGE_URI = "imageURI"
        const val ORDER_DATA_KEY = "OrderData"
        const val ORDER_CODE = "order_code"
        const val ORDER_ERROR_DETAILS = "error_details"
    }
    object Firebase {
        const val DOORS_COLLECTION_NAME = "doors"
    }
    object Placeholders {
        const val P_ORDER_CODE = "%code%"
        const val P_ORDER_ERROR_DETAILS = "%details%"
    }
}