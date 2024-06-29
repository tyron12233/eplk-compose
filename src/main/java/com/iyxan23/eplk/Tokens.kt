package com.iyxan23.eplk

enum class Tokens {

    NULL,

    COMPOSABLE,

    // Arithmetic tokens
    PLUS,
    MINUS,
    MUL,
    DIV,
    POW,

    PLUS_EQUAL,
    MINUS_EQUAL,
    MUL_EQUAL,
    DIV_EQUAL,

    DOUBLE_PLUS,
    DOUBLE_MINUS,

    VAR,

    // Comparison tokens
    DOUBLE_EQUALS,
    GREATER_THAN,
    LESSER_THAN,
    GREATER_OR_EQUAL_THAN,
    LESSER_OR_EQUAL_THAN,
    NOT,
    NOT_EQUAL,
    AND,
    OR,

    TRUE,
    FALSE,

    IF,
    ELIF,
    ELSE,
    BRACES_OPEN,  // {
    BRACES_CLOSE, // }

    FOR,
    SEMICOLON,
    COLON,

    FUN,
    COMMA,
    ARROW,

    WHILE,

    EQUAL,

    PAREN_OPEN,  // (
    PAREN_CLOSE, // )

    STRING_LITERAL,
    INT_LITERAL,
    FLOAT_LITERAL,

    BRACKET_OPEN,  // [
    BRACKET_CLOSE, // ]

    IDENTIFIER,

    RETURN,
    CONTINUE,
    BREAK,

    DOT,

    NEWLINE,

    EOF, // End Of File
}