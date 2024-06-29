@file:OptIn(ExperimentalSplitPaneApi::class)

package com.tyron.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.iyxan23.eplk.interpreter.StandardDefinitions
import com.iyxan23.eplk.lexer.Lexer
import com.iyxan23.eplk.nodes.Node
import com.iyxan23.eplk.parser.Parser
import com.iyxan23.eplk.objects.NativeEplkObject
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState
import org.jetbrains.skiko.Cursor
import javax.swing.JFrame
import javax.swing.SwingUtilities

val composeScope = ComposeDefinitions.generateComposeScope("compose")
val scope = StandardDefinitions.generateScope("<SHELL>", parentScope = composeScope)


fun main() {

    val template = """
        composable fun App(title) {
            Column(
                content = () {
                    Text(title)
                    Text("Hi")
                }
            )
        }

        
        App("Title")
    """.trimIndent()

    SwingUtilities.invokeLater {

        val window = JFrame()
        val panel = ComposePanel()

        panel.setContent {

            var code = remember { mutableStateOf(template) }

            var interpreterState = remember { mutableStateOf(InterpreterState()) }

            LaunchedEffect(code.value) {
                interpreterState.value = interpreterState.value.copy(inProgress = true)

                runCatching {
                    interpretCode(code.value)
                }.onSuccess { interpreterResult ->
                    interpreterState.value = InterpreterState(node = interpreterResult)
                }.onFailure { exception ->
                    interpreterState.value = interpreterState.value.copy(
                        hasError = true,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }.also {
                    interpreterState.value = interpreterState.value.copy(inProgress = false)
                }
            }
            HorizontalSplitPane(
                modifier = Modifier.fillMaxSize(),
                splitPaneState = rememberSplitPaneState(0.5f)
            ) {
                first {
                    when {
                        interpreterState.value.inProgress -> CircularProgressIndicator(
                            Modifier.fillMaxSize().wrapContentSize(Alignment.Center)
                        )

                        interpreterState.value.hasError -> {
                            Box(Modifier.fillMaxSize().background(Color(0xFFE57373))) {
                                Text(
                                    interpreterState.value.error!!,
                                    Modifier.fillMaxSize().wrapContentSize(Alignment.Center),
                                    style = MaterialTheme.typography.body1,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        interpreterState.value.node != null -> {
                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val composer = currentComposer
                                remember {
                                    composeScope.symbolTable.symbols["\$injectedComposer"] =
                                        NativeEplkObject(composer, composeScope)
                                }

                                val result = interpreterState.value.node!!.visit(scope)
                                if (result.hasError) {
                                    interpreterState.value = interpreterState.value.copy(
                                        hasError = true,
                                        error = result.error!!.generateString()
                                    )
                                }
                            }
                        }
                    }
                }

                second { BasicTextField(code.value, { code.value = it }, Modifier.padding(8.dp)) }

                splitter {
                    visiblePart { Box(Modifier.width(1.dp).fillMaxHeight().background(Color.Black)) }
                    handle {
                        Box(
                            Modifier
                                .markAsHandle()
                                .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                                .background(SolidColor(Color.Gray), alpha = 0.50f)
                                .width(9.dp)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }



        window.contentPane = panel
        window.pack()

        window.setSize(1080, 720)
        window.setLocationRelativeTo(null)
        window.isVisible = true
    }
}

data class InterpreterState(
    val node: Node? = null,
    val hasError: Boolean = false,
    val error: String? = null,
    val inProgress: Boolean = true
)

fun interpretCode(code: String): Node {
    return runCatching {
        Lexer("<SHELL>", code).doLexicalAnalysis()
    }.mapCatching { lexerResult ->
        lexerResult.takeIf { it.error == null }
            ?: throw Exception(lexerResult.error!!.generateString())
    }.mapCatching {
        Parser(it.tokens!!).parse()
    }.mapCatching { parseResult ->
        parseResult.takeIf { !it.hasError }
            ?: throw Exception(parseResult.error!!.generateString())
    }.getOrElse { throw it }.node!!
}