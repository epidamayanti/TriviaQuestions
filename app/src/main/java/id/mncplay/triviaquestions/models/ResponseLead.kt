package id.mncplay.triviaquestions.models

data class ResponseLead (
    val status : Boolean,
    val message :String,
    val data : MutableList<DataUser>
)
