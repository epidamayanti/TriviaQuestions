package id.mncplay.triviaquestions.models

data class ResponseData (
    val response_code : Int,
    val results : MutableList<DataQuestions>
)
