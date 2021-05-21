package club.sk1er.elementa.components.image

import club.sk1er.elementa.utils.drawTexture
import gg.essential.universal.UGraphics
import gg.essential.universal.utils.ReleasedDynamicTexture
import java.awt.Color
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

object DefaultLoadingImage : ImageProvider {
    private var loadingImage: BufferedImage? = ImageIO.read(this::class.java.getResourceAsStream("/loading.png"))
    private lateinit var loadingTexture: ReleasedDynamicTexture

    override fun drawImage(x: Double, y: Double, width: Double, height: Double, color: Color) {
        if (!::loadingTexture.isInitialized) {
            loadingTexture = UGraphics.getTexture(loadingImage!!)
            loadingImage = null
        }

        drawTexture(loadingTexture, color, x, y, width, height)
    }
}
