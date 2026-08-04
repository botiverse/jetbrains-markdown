package org.intellij.markdown.parser

import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.gfm.GFMConstraints
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import org.intellij.markdown.flavours.gfm.GFMMarkerProcessor
import org.intellij.markdown.parser.constraints.MarkdownConstraints
import kotlin.test.Test
import kotlin.test.assertEquals

class ParagraphLazyContinuationPolicyTest {
    /**
     * Ends paragraphs whenever the lazily-continued line drops a block-quote
     * constraint: chat dialects render `> quote\nreply` as a quote followed by
     * a plain paragraph instead of absorbing the reply into the quote.
     */
    private class NoBlockQuoteLazyContinuationFlavour : GFMFlavourDescriptor() {
        override val markerProcessorFactory: MarkerProcessorFactory = object : MarkerProcessorFactory {
            override fun createMarkerProcessor(productionHolder: ProductionHolder): MarkerProcessor<*> {
                return GFMMarkerProcessor(
                    productionHolder,
                    GFMConstraints.BASE,
                    paragraphLazyContinuationPolicy = ::allowUnlessBlockQuoteDropped,
                )
            }
        }
    }

    @Test
    fun defaultBehaviourStillLazilyContinuesIntoTheQuote() {
        val tree = MarkdownParser(GFMFlavourDescriptor())
            .buildMarkdownTreeFromString("> quoted line\nreply line")

        assertEquals(listOf(MarkdownElementTypes.BLOCK_QUOTE), tree.children.blockTypes())
    }

    @Test
    fun policyEndsTheQuoteAtTheUnmarkedLine() {
        val tree = MarkdownParser(NoBlockQuoteLazyContinuationFlavour())
            .buildMarkdownTreeFromString("> quoted line\nreply line")

        assertEquals(
            listOf(MarkdownElementTypes.BLOCK_QUOTE, MarkdownElementTypes.PARAGRAPH),
            tree.children.blockTypes(),
        )
    }

    @Test
    fun consecutiveQuoteLinesStayOneQuote() {
        val tree = MarkdownParser(NoBlockQuoteLazyContinuationFlavour())
            .buildMarkdownTreeFromString("> first\n> second")

        assertEquals(listOf(MarkdownElementTypes.BLOCK_QUOTE), tree.children.blockTypes())
    }

    @Test
    fun listLazyContinuationIsUntouchedByTheQuotePolicy() {
        val markdown = "- item text\ncontinuation line"
        val default = MarkdownParser(GFMFlavourDescriptor()).buildMarkdownTreeFromString(markdown)
        val gated = MarkdownParser(NoBlockQuoteLazyContinuationFlavour()).buildMarkdownTreeFromString(markdown)

        assertEquals(default.children.blockTypes(), gated.children.blockTypes())
        assertEquals(listOf(MarkdownElementTypes.UNORDERED_LIST), gated.children.blockTypes())
    }

    private companion object {
        fun allowUnlessBlockQuoteDropped(
            paragraphConstraints: MarkdownConstraints,
            nextLineConstraints: MarkdownConstraints,
        ): Boolean {
            for (index in nextLineConstraints.types.size until paragraphConstraints.types.size) {
                if (paragraphConstraints.types[index] == '>') return false
            }
            return true
        }

        fun List<ASTNode>.blockTypes() =
            map { it.type }.filter { it != org.intellij.markdown.MarkdownTokenTypes.EOL }
    }
}
