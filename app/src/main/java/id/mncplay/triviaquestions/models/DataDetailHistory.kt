package id.mncplay.triviaquestions.models

data class DataDetailHistory (
    val id : String,
    val idHistory : String,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val trueAnswer: String,
    val answer: String
)