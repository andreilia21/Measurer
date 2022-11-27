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
        object Orders {
            object Collections {
                const val ORDERS = "orders"
            }

            object Fields {
                const val EMAIL = "email"
                const val ORDER_CODE = "order_code"
                const val MATERIAL = "material"
                const val WIDTH = "width"
                const val HEIGHT = "height"
                const val AREA = "area"
            }
        }

        object OrderCode {
            object Collections {
                const val ORDER_CODE = "order_code"
            }

            object Fields {
                const val ORDER_CODE = "order_code"
            }
        }
    }

    object Placeholders {
        const val P_ORDER_CODE = "%code%"
        const val P_ORDER_ERROR_DETAILS = "%details%"
    }
}