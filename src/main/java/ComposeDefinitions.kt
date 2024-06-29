@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.tyron.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.reflect.ComposableMethod
import androidx.compose.runtime.reflect.asComposableMethod
import com.iyxan23.eplk.errors.EplkRuntimeError
import com.iyxan23.eplk.interpreter.RealtimeResult
import com.iyxan23.eplk.interpreter.Scope
import com.iyxan23.eplk.lexer.models.Position
import com.iyxan23.eplk.objects.EplkNativeFunction
import com.iyxan23.eplk.objects.EplkNativeFunction.Companion.createParameters
import com.iyxan23.eplk.objects.EplkObject
import com.iyxan23.eplk.objects.EplkVoid
import com.tyron.compose.com.iyxan23.eplk.nodes.compose.getComposer
import java.lang.reflect.Method

object ComposeDefinitions {

    fun generateComposeScope(name: String): Scope {
        val scope = Scope(name)
        val composableWrapMethod = getComposableWrapMethod() // Extract method retrieval

        scope.symbolTable.symbols["Text"] = EplkNativeFunction(
            scope,
            "Text",
            createParameters("text", "style", "\$composer", "\$changed") // Explicit parameters
        ) { funcScope, arguments, startPosition, endPosition, func ->
            val text = arguments["text"] ?: return@EplkNativeFunction errorResult(
                "ArgumentNotFound", "Argument 'text' is required", startPosition, endPosition, funcScope
            )

            val composer = funcScope.getComposer()

            // TODO: add compose compiler generated magic here e.g. generate the magic bits for tracking
            //  changes in composable parameters
            //  @see https://github.com/JetBrains/compose-multiplatform-core/blob/jb-main/compose/compiler/compiler-hosted/src/main/java/androidx/compose/compiler/plugins/kotlin/lower/ComposableFunctionBodyTransformer.kt


            composableWrapMethod.invoke(composer, this, @Composable {
                Text(text.toString())
            })

            successResult(EplkVoid(scope))
        }



        scope.symbolTable.symbols["Column"] = defineComposableFunction(scope, "Column") {
                content, startPosition, endPosition ->
            composableWrapMethod.invoke(scope.getComposer(), this, @Composable {
                Column { content.call(emptyMap(), startPosition, endPosition) }
            })
        }

        scope.symbolTable.symbols["Row"] = defineComposableFunction(scope, "Row") {
                content, startPosition, endPosition ->
            composableWrapMethod.invoke(scope.getComposer(), this, @Composable {
                Row { content.call(emptyMap(), startPosition, endPosition) }
            })
        }



        return scope
    }

    private fun defineComposableFunction(scope: Scope, name: String, content: (EplkObject, Position, Position) -> Unit): EplkNativeFunction {
        return EplkNativeFunction(scope, name, createParameters("content", "\$composer", "\$changed")) {
                funcScope, arguments, startPosition, endPosition, _ ->
            val contentArg = arguments["content"] ?: return@EplkNativeFunction errorResult(
                "ArgumentNotFound", "Argument 'content' is required", startPosition, endPosition, funcScope
            )

            content(contentArg, startPosition, endPosition)
            successResult(EplkVoid(scope))
        }
    }

    private fun successResult(value: EplkObject): RealtimeResult<EplkObject> =
        RealtimeResult<EplkObject>().success(value)

    private fun errorResult(type: String, message: String, start: Position, end: Position, scope: Scope): RealtimeResult<EplkObject> =
        RealtimeResult<EplkObject>().failure(EplkRuntimeError(type, message, start, end, scope))

    // Method to get the Composable wrap method (extracted for clarity)
    private fun getComposableWrapMethod(): ComposableMethod {
        return ComposeDefinitions::class.java.declaredMethods
            .filter { it.name == "wrap" }
            .firstOrNull()
            ?.asComposableMethod() ?: throw RuntimeException("Wrap method not found")
    }


    // this is a hack !
    // we can only invoke composable functions inside a composable function
    // this check is done by the compose compiler plugin, they have this check because
    // the plugin injects $composer and $changed, but we already do this ourselves.
    // we call the method at runtime instead to bypass this checks
    @Suppress("unused")
    @Composable
    fun wrap(content: @Composable () -> Unit) {
        content()
    }
}



