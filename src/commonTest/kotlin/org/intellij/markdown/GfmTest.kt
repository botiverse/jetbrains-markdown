package org.intellij.markdown

import kotlin.test.Test

class GfmTest: SpecTest(org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor()) {
    @Test
    fun testAutolinkInsideATag() = doTest(
        markdown = "<a href=\"https://jb.gg\">https://www.jb.gg/?q=19</a>",
        html = "<p><a href=\"https://jb.gg\"><a href=\"https://www.jb.gg/?q=19\">https://www.jb.gg/?q=19</a></a></p>"
    )

    @Test
    fun testAutolinkWithBoldEmphasis() = doTest(
        markdown = "**https://xxxx.com**(x)",
        html = "<p><strong><a href=\"https://xxxx.com\">https://xxxx.com</a></strong>(x)</p>"
    )

    @Test
    fun testAutolinkPathWithBoldEmphasis() = doTest(
        markdown = "**https://xxxx.com/path**(x)",
        html = "<p><strong><a href=\"https://xxxx.com/path\">https://xxxx.com/path</a></strong>(x)</p>"
    )

    @Test
    fun testAutolinkPathWithItalicEmphasis() = doTest(
        markdown = "*https://xxxx.com/path*(x)",
        html = "<p><em><a href=\"https://xxxx.com/path\">https://xxxx.com/path</a></em>(x)</p>"
    )

    @Test
    fun testAutolinkPathWithStrikethrough() = doTest(
        markdown = "~~https://xxxx.com/path~~(x)",
        html = "<p><span class=\"user-del\"><a href=\"https://xxxx.com/path\">https://xxxx.com/path</a></span>(x)</p>"
    )

    @Test
    fun testAutolinkPathWithUnderscoreBold() = doTest(
        markdown = "__https://xxxx.com/path__(x)",
        html = "<p><strong><a href=\"https://xxxx.com/path\">https://xxxx.com/path</a></strong>(x)</p>"
    )

    @Test
    fun testAutolinkPathWithUnderscoreItalic() = doTest(
        markdown = "_https://xxxx.com/path_(x)",
        html = "<p><em><a href=\"https://xxxx.com/path\">https://xxxx.com/path</a></em>(x)</p>"
    )

    @Test
    fun testAutolinkWithParenPathAndBold() = doTest(
        markdown = "**https://en.wikipedia.org/wiki/Mercury_(planet)**(x)",
        html = "<p><strong><a href=\"https://en.wikipedia.org/wiki/Mercury_(planet)\">https://en.wikipedia.org/wiki/Mercury_(planet)</a></strong>(x)</p>"
    )

    @Test
    fun testAutolinkWithParenPathBare() = doTest(
        markdown = "https://en.wikipedia.org/wiki/Mercury_(planet)",
        html = "<p><a href=\"https://en.wikipedia.org/wiki/Mercury_(planet)\">https://en.wikipedia.org/wiki/Mercury_(planet)</a></p>"
    )
}