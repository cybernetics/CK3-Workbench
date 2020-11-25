package com.github.xetra11.ck3workbench

import kotlin.reflect.KProperty

interface ScriptValidator {
    fun validate(scriptToValidate: String): ValidationResult

    /*
    * Represents the status of a validation.
    */
    class ValidationResult(
        val errors: List<ValidationError> = emptyList(),
        val warnings: List<ValidationWarning> = emptyList()
    ) {
        val hasErrors: Boolean
            get() = errors.isNotEmpty()
        val hasWarnings: Boolean
            get() = warnings.isNotEmpty()
    }

    data class ValidationError(
        val reason: String
    )

    data class ValidationWarning(
        val reason: String
    )

    fun ValidationResult.error(error: ValidationError) = ValidationResult(
        listOf(error)
    )
}
