package com.tyron.compose.com.iyxan23.eplk.nodes.variable

import com.iyxan23.eplk.errors.EplkRuntimeError
import com.iyxan23.eplk.interpreter.RealtimeResult
import com.iyxan23.eplk.interpreter.Scope
import com.iyxan23.eplk.lexer.models.Position
import com.iyxan23.eplk.nodes.Node
import com.iyxan23.eplk.objects.EplkObject

class FieldAccessNode(
    val name: String,
    val access: Node,
    override val startPosition: Position
) : Node() {

    override val endPosition: Position = access.endPosition
    override fun visit(scope: Scope): RealtimeResult<EplkObject> {
        val thisVariable = scope.searchVariable(name) ?: return RealtimeResult<EplkObject>().failure(
            EplkRuntimeError("UndefinedVariable", "Variable $name is not defined", startPosition, endPosition, scope)
        )

        val result = access.visit(thisVariable.scope)

        return result
    }
}