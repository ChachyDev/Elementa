package gg.essential.elementa.constraints.resolution

import gg.essential.elementa.UIComponent
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.*
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.OutlineEffect
import gg.essential.elementa.effects.ScissorEffect
import java.awt.Color

class ConstraintResolutionGui(
    private val guiName: String,
    private val gui: UIComponent,
    private val nodes: List<ResolverNode>?
) : WindowScreen() {
    init {
        UIBlock(Color(22, 22, 24)).constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            x = 10.percent()
            y = 10.percent()
            width = 1.pixel()
            height = 80.percent()
        } childOf window

        val container = UIContainer().constrain {
            x = SiblingConstraint()
            y = 10.percent()
            width = 80.percent() - 2.pixels()
            height = 80.percent()
        } effect ScissorEffect() childOf window

        UIBlock(Color(80, 80, 80)).constrain {
            x = SiblingConstraint()
            y = 10.percent()
            width = 1.pixel()
            height = 100.percent()
            height = 80.percent()
        } childOf window

        val titleContent = UIContainer().constrain {
            x = 1.pixel()
            width = 100.percent() - 2.pixels()
            height = ChildBasedSizeConstraint()
        } childOf container

        val titleText = UIText("Cyclic Constraint Tree Detected").constrain {
            x = CenterConstraint()
            textScale = 2.pixels()
            color = Color(239, 83, 80).toConstraint()
        } childOf titleContent

        val tabContainer = UIContainer().constrain {
            y = SiblingConstraint(10f) boundTo titleContent
            width = 100.percent()
            height = ChildBasedSizeConstraint()
        } childOf container

        val tabContent = UIContainer().constrain {
            x = CenterConstraint()
            width = 50.percent()
            height = ChildBasedMaxSizeConstraint() + 2.pixels()
        } childOf tabContainer

        val tabHighlight = UIBlock(Color(200, 200, 200)).constrain {
            y = (-2).pixels(alignOpposite = true)
            width = 50.percent() boundTo tabContent
            height = 2.pixels()
        } childOf tabContent

        val inactiveText = Color(187, 187, 187).toConstraint()
        val activeText = Color.WHITE.toConstraint()

        val listTab = UIContainer().constrain {
            width = 50.percent()
            height = ChildBasedSizeConstraint() + 10.pixels()
        } childOf tabContent

        val listTabText = UIText("Bad Constraint Path").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = activeText
        } childOf listTab

        val treeTab = UIContainer().constrain {
            x = SiblingConstraint()
            width = 50.percent()
            height = ChildBasedSizeConstraint() + 10.pixels()
        } childOf tabContent

        val treeTabText = UIText("Component Hierarchy").constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            color = inactiveText
        } childOf treeTab

        val displayContent = UIContainer().constrain {
            x = 1.pixel()
            y = SiblingConstraint(30f)
            width = 100.percent() - 2.pixels()
            height = 100.percent()
        } childOf container

        val listView = ListView().constrain {
            x = CenterConstraint()
        } childOf displayContent

        val treeView = TreeView(gui).constrain {
            width = 100.percent()
            height = 100.percent()
        }

        var listSelected = true

        listTab.onMouseEnter {
            if (listSelected)
                return@onMouseEnter

            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, activeText)
            }
        }.onMouseLeave {
            if (listView.hasParent)
                return@onMouseLeave

            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
        }.onMouseClick {
            if (listSelected)
                return@onMouseClick

            listSelected = true

            displayContent.replaceChild(listView, treeView)
            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
            tabHighlight.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
            }
        }

        treeTab.onMouseEnter {
            if (!listSelected)
                return@onMouseEnter

            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, activeText)
            }
        }.onMouseLeave {
            if (!listSelected)
                return@onMouseLeave

            treeTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
        }.onMouseClick {
            if (!listSelected)
                return@onMouseClick

            listSelected = false

            displayContent.replaceChild(treeView, listView)
            listTabText.animate {
                setColorAnimation(Animations.OUT_EXP, 0.5f, inactiveText)
            }
            tabHighlight.animate {
                setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels(alignOpposite = true))
            }
        }

        UIBlock(Color(80, 80, 80)).constrain {
            x = (-1).pixels(alignOpposite = true)
            width = 1.pixel()
            height = 100.percent()
        } childOf container
    }

    private inner class ListView : UIContainer() {
        init {
            constrain {
                width = ChildBasedMaxSizeConstraint()
                height = 100.percent()
            }

            UIText("Open Screen name: $guiName").constrain {
                y = SiblingConstraint(15f)
                textScale = 1.25f.pixels()
            } childOf this

            if (nodes != null) {
                UIText("Cyclic constraints:").constrain {
                    y = SiblingConstraint(15f)
                    textScale = 1.25f.pixels()
                } childOf this

                ConstraintPathComponent().constrain {
                    y = SiblingConstraint(10f)
                    height = 100.percent()
                } childOf this
            } else {
                UIWrappedText("Unfortunately Elementa is unable to determine the constraints responsible. This is most likely due to the use of basicConstraints.").constrain {
                    width = 300.pixels()
                    color = Color(239, 83, 80).toConstraint()
                } childOf this
            }
        }
    }

    private inner class ConstraintPathComponent : UIContainer() {
        init {
            constrain {
                width = ChildBasedSizeConstraint()
            }

            val scrollComponent = ScrollComponent() childOf this

            val pathItems = nodes!!.indices.map { index ->
                ConstraintPathItem(index).constrain {
                    y = SiblingConstraint(15f)
                } childOf scrollComponent
            }

            scrollComponent.constrain {
                width = basicWidthConstraint {
                    pathItems.map { it.getWidth() }.maxOrNull()!!
                }
                height = 100.percent()
            }
        }
    }

    private inner class ConstraintPathItem(index: Int) : UIContainer() {
        private val node = nodes!![index]

        init {
            constrain {
                height = ChildBasedMaxSizeConstraint()
                width = ChildBasedSizeConstraint()
            }

            val right = UIContainer().constrain {
                x = SiblingConstraint(10f)
                width = ChildBasedMaxSizeConstraint()
                height = ChildBasedSizeConstraint() + 4.pixels()
            }

            val left = UIContainer().constrain {
                width = ChildBasedSizeConstraint()
                height = basicHeightConstraint { right.getHeight() }
            } childOf this

            UIText("${index + 1}. ").constrain {
                textScale = 1.25f.pixels()
            } childOf left

            right childOf this

            if (index != nodes!!.lastIndex) {
                UIText("§7Component: §r${node.component.componentName}@${Integer.toHexString(node.component.hashCode())}").constrain {
                    y = SiblingConstraint()
                } childOf right

                UIText("§7Constraint: §r${node.constraint.javaClass.simpleName}").constrain {
                    y = SiblingConstraint(2f)
                } childOf right

                UIText("§7Constraint Type: §r${node.constraintType.prettyName}").constrain {
                    y = SiblingConstraint(2f)
                } childOf right
            } else {
                UIText("§7The first entry in this list") childOf right
            }
        }
    }

    private inner class TreeView(rootComponent: UIComponent) : UIContainer() {
        init {
            val rootNode = componentToNode(rootComponent)

            TreeGraphComponent(rootNode, TreeGraphStyle().copy(isHorizontal = true, heightBetweenRows = 20f)).constrain {
                x = 5.percent()
                width = 90.percent()
                height = 100.percent()
            } childOf this
        }

        private fun componentToNode(component: UIComponent): TreeGraphNode {
            return ConstraintTreeNode(component).withChildren {
                component.children.forEach {
                    add(componentToNode(it))
                }
            }
        }
    }

    private inner class ConstraintTreeNode(private val target: UIComponent) : TreeGraphNode() {
        override fun makeComponent(): UIComponent {
            val block = UIBlock(Color(50, 50, 50)).constrain {
                width = ChildBasedSizeConstraint() + 3.pixels()
                height = ChildBasedSizeConstraint() + 3.pixels()
            } effect OutlineEffect(Color(100, 100, 100), 1f)

            val hasError = nodes?.any { it.component == target } ?: false
            val colorCode = if (hasError) "§c" else ""

            UIText("$colorCode${target.componentName}@${Integer.toHexString(target.hashCode())}").constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                textScale = 0.5f.pixels()
            } childOf block

            return block
        }
    }
}
