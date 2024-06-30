import com.iyxan23.eplk.Utils
import com.iyxan23.eplk.lexer.Lexer
import com.iyxan23.eplk.parser.Parser
import com.iyxan23.eplk.nodes.Node
import kotlin.test.Test

class ParserTest {

    private val filename = "<TEST>"

    private fun expectNodes(code: String, expectation: Node) {
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        // TODO: 6/16/21
        //  don't put the entire node tree as the truth data, make a new function that
        //  checks the important part of the node
        assert(result.node == expectation)
    }

    @Test
    fun binOpTest() {
        val code = "1 + 2"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun unaryTest() {
        val code = "-1 + 2"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun stringTest() {
        val code = "\"Hello World\n\""
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun parenthesesTest() {
        val code = "(1 + 2) * 3"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun powTest() {
        val code = "3 ^ 3"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun variableDeclarationTest() {
        val code = "var hello_world = 1 + 1"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun variableAccessTest() {
        val code = "1 + hello_world"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError)

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun comparisonExpressionTest() {
        val code = "5 + 12 / variable > 10 + 5 * variable"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun ifTest() {
        val code = "var hello_world = if (1 == 1) 10 else 0"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun ifTest2() {
        val code = "var hello_world = if (1 == 1) 10 elif (2 == 2) 2 else 0"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun ifTest3() {
        val code = "var hello_world = if (1 == 1) 10 elif (2 == 2) 2 elif (3 == 3) 3 else 0"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun forTest() {
        val code = "for (var b = 0; b < 10; var b = b + 1) 1 + 1"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun whileTest() {
        val code = "while (variable > 10) var something = something * 2"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun multilineWhileTest() {
        val code = "while (variable > 10) {\n    var something = something * 2\n    var hello = some_function() * 10\n}"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun funcDefTest() {
        val code = "fun hello_world(hi, world, test) -> 1 + 1"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun multilineFuncDefTest() {
        val code = "fun hello_world(hi, world, test) {\n    var a = 10\n    println(\"Hello World\")\n}"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun funcCallNoArgsTest() {
        val code = "hello_world()"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun funcCallWithArgsTest() {
        val code = "hello_world(10, 10 + 10, 100 * 2)"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun listTest() {
        val code = "[1, \"Hi\", -1, 1 + 1]"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun emptyListTest() {
        val code = "[]"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun incrementTest() {
        val code = "a++"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun decrementTest() {
        val code = "a--"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun statementsTest() {
        val code = "1 + 1\n5 * 2\n\n10 / 50\n\n\n\n     \n\n\n 10 * 10"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun equalOperatorsTest() {
        val code = "a += 1\nb -= 3\nc *= 10\nd /= 2"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }

        println(Utils.prettyPrintNode(result.node!!))
    }

    @Test
    fun syntaxErrorTest() {
        val code = "1 2 3 /"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(result.hasError)
    }

    @Test
    fun syntaxErrorTest2() {
        val code = "-(-1"
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(result.hasError)
    }

    @Test
    fun multilineFuncCallTest() {
        val code = """
            hello_world(
                10,
                10 + 10,
                100 * 2
            )
        """.trimIndent()

        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun lambdaFuncArgumentTest() {
        val code = """
            LambdaFunc(
                () { }
            )
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun composableTest() {
        val code = """
            composable fun App(title) {
                Column(
                    content = () {
                        Text(title)
                    }
                )
            }
            
            App("Title")
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun emptyFunctionTest() {
        val code = """
            fun helloWorld() { }
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun multiLineEmptyFunction() {
        val code = """
            fun helloWorld() {
            
            }
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun emptyLambdaTest() {
        val code = """
            LambdaFunc(
                () { }
            )
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun multiLineEmptyLambdaTest() {
        val code = """
            LambdaFunc(
                () {
                
                }
            )
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun fieldAccessExpr() {
        val code = """
            var a = object.field
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        Utils.prettyPrintNode(result.node!!)

        assert(!result.hasError) { println(result.error!!.toString()) }
    }

    @Test
    fun incrementFieldAccessExpr() {
        val code = """
            object.field += 1
        """.trimIndent()
        val tokens = Lexer(filename, code).doLexicalAnalysis().tokens!!
        val result = Parser(tokens).parse()

        Utils.prettyPrintNode(result.node!!)

        assert(!result.hasError) { println(result.error!!.toString()) }
    }
}