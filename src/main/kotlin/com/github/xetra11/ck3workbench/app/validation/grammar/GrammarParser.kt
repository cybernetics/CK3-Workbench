package com.github.xetra11.ck3workbench.app.validation.grammar

import com.github.xetra11.ck3workbench.app.validation.grammar.GrammarValidator.TokenDefinition
import com.github.xetra11.ck3workbench.app.validation.grammar.GrammarValidator.TokenType
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Reads a grammar file and returns the grammar definition for it
 *
 * @author Patrick C. Höfer aka "xetra11"
 */
open class GrammarParser {
    private val resolverDictionary: MutableMap<String, MutableList<String>> = mutableMapOf()

    open fun process(grammarInput: String): Grammar {
        var outputGrammar: Grammar = Grammar("", listOf<TokenType>())
        val rawGrammars = grammarInput
            .trim()
            .split("---")

        rawGrammars.forEach { grammar ->
            val lines = extractGrammarInputLines(grammar)
            val definitionValueName = extractDefinitionValueName(lines)
            val definitionResultName = extractDefinitionResultName(lines)
            val tokenChain = extractTokenChain(lines)
            val tokens = extractTokens(tokenChain)

            // init dictionary
            definitionValueName?.let {
                resolverDictionary.putIfAbsent(it, mutableListOf())
            }

            val typedTokens = tokens
                .quantify()
                .map { token ->
                    definitionValueName?.let {
                        resolverDictionary[it]!!.add(token)
                    }
                    token
                }
                .resolve()
                .map { GrammarMappings.mapGrammar(it) }

            definitionResultName?.let {
                outputGrammar = Grammar(it, typedTokens)
            }
        }
        return outputGrammar
    }

    private fun extractGrammarInputLines(grammar: String): List<String> {
        return grammar.lines()
            .map { it.trimWhiteSpace() }
    }

    private fun extractTokens(tokenChain: String) = tokenChain.trim().split(".")

    private fun extractTokenChain(lines: List<String>): String {
        return lines.filterNot { it.startsWith(":") || it.startsWith("=") }.joinToString("")
    }

    private fun extractDefinitionValueName(lines: List<String>): String? {
        return try {
            lines.first { it.startsWith(":") }.replace(":", "")
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun extractDefinitionResultName(lines: List<String>): String? {
        return try {
            lines.first { it.startsWith("=") }.replace("=", "")
        } catch (e: NoSuchElementException) {
            null
        }
    }

    // multiply definition depending on its quantity modifier
    // Example: [DEFINITION]*3
    private fun Iterable<String>.quantify(): List<String> {
        return this.flatMap { token ->
            if (!token.contains("*")) {
                listOf(token)
            } else {
                try {
                    val quantity = token.split("*")[1].toInt()
                    token
                        .repeat(quantity)
                        .split("*$quantity")
                        .filter { it.isNotBlank() }
                } catch (e: NumberFormatException) {
                    LOG.error("Quantity modifier is not a number")
                    listOf(token)
                }
            }
        }
    }

    // resolve references with their definitions
    private fun Iterable<String>.resolve(): List<String> {
        return this.flatMap { token ->
            val isOptional = token.contains("?")
            if (!isDefinitionToken(token)) {
                listOf(token)
            } else {
                val identifierMatcher = Regex("([A-Z])\\w+")
                val name = identifierMatcher.find(token)?.value
                val resolvedToken = (resolverDictionary[name] ?: listOf(token)).resolve()
                if (isOptional) {
                    // add ? to the end to mark it as optional
                    resolvedToken.map { it.replace("]", "?]") }
                } else {
                    resolvedToken
                }
            }
        }
    }

    private fun isDefinitionToken(token: String): Boolean {
        return token.startsWith("[:")
    }

    private fun String.trimWhiteSpace(): String {
        return this.filterNot { it.isWhitespace() }
    }

    data class Grammar(
        val definitionName: String,
        val tokenDefinition: List<TokenDefinition>
    )

    inner class UntypedGrammar(
        val definitionName: String,
        val untypedToken: List<String>
    )

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(GrammarParser::class.java)
    }
}
