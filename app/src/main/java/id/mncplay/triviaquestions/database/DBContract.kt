package id.mncplay.triviaquestions.database

import android.provider.BaseColumns

object DBContract {
    class UserEntry : BaseColumns {
        companion object {
            val TABLE_USER = "users"
            val COLUMN_USER_ID = "id"
            val COLUMN_USER_USERNAME = "username"
            val COLUMN_USER_NAME = "name"
            val COLUMN_USER_SCORE = "score"

            val TABLE_QUESTIONS_CATEGORY = "category"
            val COLUMN_QUESTION_CATEGORY_ID = "id_category"
            val COLUMN_QUESTION_CATEGORY_NAME = "name_category"

            val TABLE_HISTORY = "history"
            val COLUMN_HISTORY_ID = "id_history"
            val COLUMN_HISTORY_USERNAME = "username"
            val COLUMN_HISTORY_CATEGORY = "category"
            val COLUMN_HISTORY_MODE = "mode"
            val COLUMN_HISTORY_DATE = "date"

            val TABLE_DETAIL_HISTORY = "detailHistory"
            val COLUMN_DETAIL_HISTORY_ID = "id"
            val COLUMN_DETAIL_HISTORY_ID_HISTORY = "id_history"
            val COLUMN_DETAIL_HISTORY_QUESTION = "question"
            val COLUMN_DETAIL_HISTORY_OPTION_A = "option_a"
            val COLUMN_DETAIL_HISTORY_OPTION_B = "option_b"
            val COLUMN_DETAIL_HISTORY_OPTION_C = "option_c"
            val COLUMN_DETAIL_HISTORY_OPTION_D = "option_d"
            val COLUMN_DETAIL_HISTORY_ANSWER = "answer"
            val COLUMN_DETAIL_HISTORY_TRUE_ANSWER = "true_answer"

        }
    }
}
