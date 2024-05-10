package work.wander.directory.data.employee.remote

/**
 * Enum class representing different employee data URLs.
 *
 * Each enum constant represents a different URL for fetching employee data, along with a user-readable description.
 *
 * @property url The URL for fetching employee data.
 * @property description A user-readable description of the URL.
 */
enum class EmployeeDataUrl(
    val url: String,
    val description: String,
) {
    Complete("https://s3.amazonaws.com/sq-mobile-interview/employees.json", "Default (Valid) Endpoint"),
    Malformed("https://s3.amazonaws.com/sq-mobile-interview/employees_malformed.json", "Malformed Output Endpoint"),
    Empty("https://s3.amazonaws.com/sq-mobile-interview/employees_empty.json", "Empty Output Endpoint"),
    Unknown("unknown", "Unknown Endpoint Specified");

    /**
     * Companion object for the EmployeeDataUrl enum class.
     *
     * Provides a method to get an EmployeeDataUrl enum constant from a URL string.
     */
    companion object {
        /**
         * Returns the EmployeeDataUrl enum constant corresponding to the given URL string.
         *
         * If the URL string does not match any of the enum constants, returns Unknown.
         *
         * @param url The URL string to get the enum constant for.
         * @return The EmployeeDataUrl enum constant corresponding to the given URL string.
         */
        fun fromUrl(url: String): EmployeeDataUrl {
            return when (url) {
                Complete.url -> Complete
                Malformed.url -> Malformed
                Empty.url -> Empty
                else -> Unknown
            }
        }
    }
}