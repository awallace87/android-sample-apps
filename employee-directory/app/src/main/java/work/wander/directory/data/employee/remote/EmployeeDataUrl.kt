package work.wander.directory.data.employee.remote

enum class EmployeeDataUrl(
    val url: String,
    val description: String,
    ) {
    Default("https://s3.amazonaws.com/sq-mobile-interview/employees.json", "Default (Valid) Endpoint"),
    Malformed("https://s3.amazonaws.com/sq-mobile-interview/employees_malformed.json", "Malformed Output Endpoint"),
    Empty("https://s3.amazonaws.com/sq-mobile-interview/employees_empty.json", "Empty Output Endpoint"),
    Unknown("unknown", "Unknown Endpoint Specified");

    companion object {
        fun fromUrl(url: String): EmployeeDataUrl {
            return when (url) {
                Default.url -> Default
                Malformed.url -> Malformed
                Empty.url -> Empty
                else -> Unknown
            }
        }
    }
}