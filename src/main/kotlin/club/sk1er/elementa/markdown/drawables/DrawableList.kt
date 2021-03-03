package club.sk1er.elementa.markdown.drawables

import club.sk1er.elementa.markdown.DrawState
import club.sk1er.elementa.markdown.MarkdownConfig
import club.sk1er.elementa.markdown.cursor.TextCursor
import java.util.*

class DrawableList(
    config: MarkdownConfig,
    drawables: List<Drawable>
) : Drawable(config), List<Drawable> {
    private lateinit var drawables: List<Drawable>

    init {
        setDrawables(drawables)
    }

    fun setDrawables(newDrawables: List<Drawable>) {
        drawables = newDrawables
        trim(this)

        forEachIndexed { index, drawable ->
            if (index > 0)
                drawable.previous = this[index - 1]
            if (index != lastIndex)
                drawable.next = this[index + 1]
        }
    }

    override fun layoutImpl(x: Float, y: Float, width: Float): Layout {
        var currY = y
        forEach {
            currY += it.layout(x, currY, width).height
        }
        val height = currY - y
        return Layout(x, y, width, height)
    }

    override fun select(mouseX: Float, mouseY: Float): TextCursor {
        // Used for positioning the cursor in-between drawables if no
        // drawable is being directly hovered
        var closestDrawable: Drawable? = null
        var closestDistance = Float.MAX_VALUE
        var direction: Direction? = null

        for (drawable in drawables) {
            if (drawable.isHovered(mouseX, mouseY)) {
                return drawable.select(mouseX, mouseY)
            } else {
                if (mouseY < drawable.y) {
                    if (drawable.y - mouseY < closestDistance) {
                        direction = Direction.Up
                        closestDistance = drawable.y - mouseY
                        closestDrawable = drawable
                    }
                } else if (mouseY > drawable.y + drawable.height) {
                    if (drawable.y + drawable.height - mouseY < closestDistance) {
                        direction = Direction.Down
                        closestDistance = mouseY - (drawable.y + drawable.height)
                        closestDrawable = drawable
                    }
                } else {
                    // The drawable is hovered vertically, but not horizontally
                    closestDistance = 0f
                    closestDrawable = drawable
                    direction = if (mouseX < drawable.x) {
                        Direction.Left
                    } else Direction.Right
                    break
                }
            }
        }

        if (closestDrawable == null || closestDistance == Float.MAX_VALUE || direction == null)
            TODO()

        return when (direction) {
            Direction.Up -> closestDrawable.selectStart()
            Direction.Down -> closestDrawable.selectEnd()
            Direction.Left, Direction.Right ->
                closestDrawable.select(mouseX, mouseY)
        }
    }

    override fun selectStart() = drawables.first().selectStart()

    override fun selectEnd() = drawables.last().selectEnd()

    override fun draw(state: DrawState) {
        forEach { it.draw(state) }
    }

    override val size get() = drawables.size
    override fun contains(element: Drawable) = element in drawables
    override fun containsAll(elements: Collection<Drawable>) = drawables.containsAll(elements)
    override fun get(index: Int) = drawables[index]
    override fun indexOf(element: Drawable) = drawables.indexOf(element)
    override fun isEmpty() = drawables.isEmpty()
    override fun iterator() = drawables.iterator()
    override fun lastIndexOf(element: Drawable) = drawables.lastIndexOf(element)
    override fun listIterator() = drawables.listIterator()
    override fun listIterator(index: Int) = drawables.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int) = drawables.subList(fromIndex, toIndex)

    enum class Direction {
        Up,
        Down,
        Left,
        Right
    }
}
