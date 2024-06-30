package com.iyxan23.eplk.nodes.variable

import androidx.compose.runtime.Composer
import androidx.compose.runtime.currentComposer
import com.iyxan23.eplk.errors.EplkRuntimeError
import com.iyxan23.eplk.interpreter.RealtimeResult
import com.iyxan23.eplk.interpreter.Scope
import com.iyxan23.eplk.lexer.models.Position
import com.iyxan23.eplk.nodes.Node
import com.iyxan23.eplk.nodes.StatementsNode
import com.iyxan23.eplk.nodes.compose.ComposeFunction
import com.iyxan23.eplk.nodes.compose.getComposer
import com.iyxan23.eplk.objects.EplkFunction
import com.iyxan23.eplk.objects.EplkObject
import com.iyxan23.eplk.objects.NativeEplkObject

class LambdaNode(
    val composable: Boolean = false,
    val parameterList: ParameterListNode,
    val statements: StatementsNode,
    override val startPosition: Position,
    override val endPosition: Position
): Node() {
    override fun visit(scope: Scope): RealtimeResult<EplkObject> {
        val anonymousName = "lambda$${scope.symbolTable.symbols.size}"
        val function = if (composable) object: ComposeFunction(scope, anonymousName , parameterList, statements) {
            override fun call(
                arguments: Map<String, EplkObject>,
                startPosition: Position,
                endPosition: Position
            ): RealtimeResult<EplkObject> {

                val composer = arguments["\$composer"]?.let {
                    (it as NativeEplkObject).`object` as Composer
                } ?: return RealtimeResult<EplkObject>().failure(
                    EplkRuntimeError(
                        "ComposerNotFoundError",
                        "Composer not found, are you sure you are using this function in a compose block?",
                        startPosition,
                        endPosition,
                        parentScope
                    )
                )
                composer.startReplaceableGroup(this@LambdaNode.startPosition.hashCode())
                val result = super.call(arguments, startPosition, endPosition)
                composer.endReplaceableGroup()

                return result
            }
        } else EplkFunction(scope, anonymousName, parameterList, statements)

        return RealtimeResult<EplkObject>().success(function)
    }
}