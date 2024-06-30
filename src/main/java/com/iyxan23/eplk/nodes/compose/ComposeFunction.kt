package com.iyxan23.eplk.nodes.compose

import androidx.compose.runtime.Composer
import com.iyxan23.eplk.errors.EplkRuntimeError
import com.iyxan23.eplk.interpreter.RealtimeResult
import com.iyxan23.eplk.interpreter.Scope
import com.iyxan23.eplk.lexer.models.Position
import com.iyxan23.eplk.nodes.StatementsNode
import com.iyxan23.eplk.objects.EplkFunction
import com.iyxan23.eplk.objects.EplkInteger
import com.iyxan23.eplk.objects.EplkObject
import com.iyxan23.eplk.nodes.variable.ParameterListNode
import com.iyxan23.eplk.objects.EplkVoid
import com.iyxan23.eplk.objects.NativeEplkObject

open class ComposeFunction(
    override val parentScope: Scope,
    override val functionName: String,
    override val parameterList: ParameterListNode,
    override val statements: StatementsNode
): EplkFunction(parentScope, functionName, parameterList, statements) {

    override fun call(
        arguments: Map<String, EplkObject>,
        startPosition: Position,
        endPosition: Position
    ): RealtimeResult<EplkObject> {
        val result = RealtimeResult<EplkObject>()

        val eplkComposer = (parentScope.searchVariable("\$composer") ?: parentScope.searchVariable("\$injectedComposer")) as NativeEplkObject? ?: return result.failure(
            EplkRuntimeError(
                "ComposerNotFoundError",
                "Composer not found, are you sure you are using this function in a compose block?",
                startPosition,
                endPosition,
                parentScope
            )
        )
        var composer =  eplkComposer.`object` as Composer

        try {


            val eplkChanged = arguments["\$changed"] as EplkInteger? ?: EplkInteger(0, parentScope)
            val newComposer = composer.startRestartGroup(startPosition.index.hashCode())

            val injectedArguments = arguments.toMutableMap().also {
                it["\$composer"] = NativeEplkObject(newComposer, parentScope)
                it["\$changed"] = EplkInteger(0, parentScope)
            }

//        if (eplkChanged.value == 0 || newComposer.skipping) {
//            newComposer.skipToGroupEnd()
//        } else {
//
//        }

            val resultCall = super.call(injectedArguments, startPosition, endPosition)
            if (resultCall.shouldReturn) {
                return resultCall
            }

        } finally {
            val endRestartGroup = composer.endRestartGroup()
            val newArguments = arguments.toMutableMap().also {
                it["\$composer"] = NativeEplkObject(composer, parentScope)
                it["\$changed"] = EplkInteger(0, parentScope)
            }
            super.call(newArguments, startPosition, endPosition)
            if (endRestartGroup != null) {
                endRestartGroup.updateScope { composer, changed ->



                }
            }
        }




        return result.success(EplkVoid(parentScope))
    }
}