package com.iyxan23.eplk.errors

import com.iyxan23.eplk.interpreter.Scope
import com.iyxan23.eplk.lexer.models.Position

open class EplkRuntimeError(
    override val name: String,
    override val detail: String,
    override val startPosition: Position,
    override val endPosition: Position,
    open val scope: Scope
) : EplkError("Runtime Error", detail, startPosition, endPosition) {

    /**
     * This function generates a traceback from the scope provided in the constructor
     */
    fun generateTraceback(): String {
        var currentScope: Scope? = scope.copy()
        var currentPosition: Position? = startPosition.copy()

        val result = StringBuilder()

        result.append("Traceback: \n")

        while (currentScope != null) {
            result.appendLine(" - Filename ${currentPosition?.filename ?: "Unknown file"} inside ${currentScope.name} at line ${currentPosition?.line ?: "Unknown line"}")

            currentPosition = currentScope.parentPosition
            currentScope = currentScope.parent
        }

        return result.toString()
    }

    override fun generateString(withPosition: Boolean): String {
        return "${super.generateString(false)}\n${ if (withPosition) generateTraceback() else "" }"
    }
}