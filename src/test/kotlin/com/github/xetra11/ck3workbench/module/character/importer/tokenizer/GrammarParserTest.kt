package com.github.xetra11.ck3workbench.module.character.importer.tokenizer

import com.github.xetra11.ck3workbench.module.character.importer.tokenizer.GrammarMatcher.OptionalTokenType
import com.github.xetra11.ck3workbench.module.character.importer.tokenizer.GrammarMatcher.TokenType
import com.github.xetra11.ck3workbench.module.character.importer.tokenizer.GrammarParser.Grammar
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class GrammarParserTest {
    private val grammarParser: GrammarParser = GrammarParser()

    @Test fun `should read grammar file containing optional token definition`() {
        val actual = grammarParser.process(GRAMMAR_FILE_4)

        assertThat(actual).isEqualTo(
            Grammar(
                "OBJECT",
                listOf(
                    TokenType.OBJECT_ID, TokenType.ASSIGNMENT, TokenType.BLOCK_START,
                    OptionalTokenType.ATTRIBUTE_ID, OptionalTokenType.ASSIGNMENT, OptionalTokenType.ATTRIBUTE_VALUE,
                    TokenType.BLOCK_END
                )
            )
        )
    }

    @Test
    fun `should read simple grammar file and return a grammar definition with a sub object`() {
        val actual = grammarParser.process(GRAMMAR_FILE_3)

        assertThat(actual).isEqualTo(
            Grammar(
                "OBJECT",
                listOf(
                    TokenType.OBJECT_ID, TokenType.ASSIGNMENT, TokenType.BLOCK_START,
                    TokenType.OBJECT_ID, TokenType.ASSIGNMENT, TokenType.BLOCK_START,
                    TokenType.ATTRIBUTE_ID, TokenType.ASSIGNMENT, TokenType.ATTRIBUTE_VALUE,
                    TokenType.BLOCK_END, TokenType.BLOCK_END
                )
            )
        )
    }

    @Test
    fun `should read simple grammar file and return a grammar definition`() {
        val actual = grammarParser.process(GRAMMAR_FILE_1)

        assertThat(actual).isEqualTo(
            Grammar(
                "OBJECT",
                listOf(
                    TokenType.OBJECT_ID,
                    TokenType.ASSIGNMENT,
                    TokenType.ATTRIBUTE_VALUE
                )
            )
        )
    }

    @Test
    fun `should read simple grammar file with multiplier and return a grammar definition`() {
        val actual = grammarParser.process(MULTI_TEST)

        assertThat(actual).isEqualTo(
            Grammar(
                "MULTI",
                listOf(
                    TokenType.ATTRIBUTE_ID,
                    TokenType.ATTRIBUTE_ID,
                    TokenType.ASSIGNMENT,
                    TokenType.ATTRIBUTE_VALUE
                )
            )
        )
    }

    @Test
    fun `should read grammar file with nested attributes and return a grammar definition`() {
        val actual = grammarParser.process(GRAMMAR_FILE_2)

        assertThat(actual).isEqualTo(
            Grammar(
                "OBJECT",
                listOf(
                    TokenType.OBJECT_ID,
                    TokenType.ASSIGNMENT,
                    TokenType.BLOCK_START,
                    TokenType.ATTRIBUTE_ID,
                    TokenType.ASSIGNMENT,
                    TokenType.ATTRIBUTE_VALUE,
                    TokenType.BLOCK_END
                )
            )
        )
    }

    companion object {
        const val GRAMMAR_FILE_1 =
            """
            :ATTRIBUTE
            [ATTRIBUTE_ID].[ASSIGNMENT].[ATTRIBUTE_VALUE]
            ---
            =OBJECT
            [OBJECT_ID].[ASSIGNMENT].[ATTRIBUTE_VALUE]
        """
        const val MULTI_TEST =
            """
            =MULTI
            [ATTRIBUTE_ID]*2.[ASSIGNMENT].[ATTRIBUTE_VALUE]
        """
        const val GRAMMAR_FILE_2 =
            """
            :ATTRIBUTE
            [ATTRIBUTE_ID].[ASSIGNMENT].[ATTRIBUTE_VALUE]
            ---
            =OBJECT
            [OBJECT_ID].[ASSIGNMENT].[BLOCK_START].[:ATTRIBUTE]*1.[BLOCK_END]
        """
        const val GRAMMAR_FILE_3 =
            """
            :ATTRIBUTE
            [ATTRIBUTE_ID].[ASSIGNMENT].[ATTRIBUTE_VALUE]
            ---
            :SUB_OBJECT
            [OBJECT_ID].[ASSIGNMENT].[BLOCK_START].[:ATTRIBUTE].[BLOCK_END]
            ---
            =OBJECT
            [OBJECT_ID].[ASSIGNMENT].[BLOCK_START].[:SUB_OBJECT].[BLOCK_END]
        """

        const val GRAMMAR_FILE_4 =
            """
            :ATTRIBUTE
            [ATTRIBUTE_ID].[ASSIGNMENT].[ATTRIBUTE_VALUE]
            ---
            =OBJECT
            [OBJECT_ID].[ASSIGNMENT].[BLOCK_START].[:ATTRIBUTE?].[BLOCK_END]
        """
    }
}
