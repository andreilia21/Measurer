package com.dewerro.measurer

/**
 * Константные значения
 */
object K {

    // Локальная база данных
    object SharedPreferences {
        const val FIREBASE_USER_DATA = "user_data"
        const val FIREBASE_EMAIL = "email"
        const val FIREBASE_PASSWORD = "password"
    }

    // Аргументы при переходе между фрагментами
    object Bundle {
        // Галлерея
        const val GALLERY_IMAGE_URI = "imageURI"

        // Заказы
        const val ORDER_DATA_KEY = "order_data"
        const val ORDER_CODE = "order_code"
        const val ORDER_ERROR_DETAILS = "error_details"

        // Прочее
        const val MEASUREMENT_OBJECT_CHOICE = "measurement_object_choice"
    }

    // Константные значения, связанные с фрагментами
    object Fragments {
        // Фрагмент обработки заказа
        object OrderProcessingFragment {
            // Время ожидания обработки
            const val PROCESS_WAIT_MILLIS = 2500L
        }
    }

    // Значения базы данных
    object Firebase {

        // Таблица с заказами
        object Orders {

            // Путь к таблице
            object Collections {
                const val ORDERS = "orders"
            }

            // Поля
            object Fields {
                const val EMAIL = "email"
                const val ORDER_CODE = "order_code"
                const val MATERIAL = "material"
                const val WIDTH = "width"
                const val HEIGHT = "height"
                const val AREA = "area"
            }
        }

        // Таблица с кодом
        object OrderCode {

            // Путь к таблице
            object Collections {
                const val ORDER_CODE = "order_code"
            }

            // Поля
            object Fields {
                const val ORDER_CODE = "order_code"
            }
        }
    }

    // Переменные, которые заменяются в тексте
    object Placeholders {
        const val P_ORDER_CODE = "%code%"
        const val P_ORDER_ERROR_DETAILS = "%details%"
        const val P_AREA = "%area%"
        const val P_LENGTH = "%length%"
    }
}