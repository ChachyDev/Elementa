package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.dsl.width
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.mods.core.universal.UGraphics

class TextDrawable(
    config: MarkdownConfig,
    text: String,
    val isBold: Boolean,
    val isItalic: Boolean
) : Drawable(config) {
    // Used by HeaderDrawable
    var scaleModifier = 1f

    private var formattedText = buildString {
        if (isBold)
            append("§l")
        if (isItalic)
            append("§o")
        append(text)
    }

    fun ensureTrimmed() {
        val styleChars = styleChars()
        formattedText = formattedText.substring(0, styleChars) +
            formattedText.substring(styleChars, formattedText.length).trimStart()
    }

    private fun styleChars() = (if (isBold) 2 else 0) + (if (isItalic) 2 else 0)

    fun width() = formattedText.width(scaleModifier)

    // Returns null if this drawable cannot be split in a way that doesn't
    // break a word. This means that the drawable should just be drawn on
    // the next line
    fun split(maxWidth: Float, breakWords: Boolean = false): Pair<TextDrawable, TextDrawable>? {
        val styleChars = styleChars()
        val plainText = formattedText.drop(styleChars)

        var splitPoint = formattedText.indices.drop(styleChars).firstOrNull {
            formattedText.substring(0, it + 1).width(scaleModifier) > maxWidth
        }

        if (splitPoint == null)
            TODO()

        splitPoint = splitPoint - 1 - styleChars

        if (!breakWords) {
            while (splitPoint > styleChars && formattedText[splitPoint - 1] != ' ')
                splitPoint--

            if (splitPoint == styleChars)
                return null
        }

        val first = TextDrawable(config, plainText.substring(0, splitPoint), isBold, isItalic)
        val second = TextDrawable(config, plainText.substring(splitPoint, plainText.length), isBold, isItalic)
        first.scaleModifier = scaleModifier
        second.scaleModifier = scaleModifier
        return first to second
    }

    override fun layoutImpl(): Height {
        return 9f * scaleModifier
    }

    override fun draw() {
        UGraphics.scale(scaleModifier, scaleModifier, 1f)

        if (config.paragraphConfig.hasShadow) {
            UGraphics.drawString(
                formattedText,
                x / scaleModifier,
                y / scaleModifier,
                config.paragraphConfig.color.rgb,
                config.paragraphConfig.shadowColor.rgb
            )
        } else {
            UGraphics.drawString(
                formattedText,
                x / scaleModifier,
                y / scaleModifier,
                config.paragraphConfig.color.rgb,
                false
            )
        }

        UGraphics.scale(1f / scaleModifier, 1f / scaleModifier, 1f)
    }

    override fun toString() = formattedText
}