package id.mncplay.triviaquestions.models

data class DataQuestions (
    val category: String,
    val type: String,
    val difficulty: String,
    val question: String,
    val correct_answer: String,
    val incorrect_answers: MutableList<String>
)