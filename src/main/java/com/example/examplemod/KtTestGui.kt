package com.example.examplemod

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.*
import club.sk1er.elementa.components.input.UIPasswordInput
import club.sk1er.elementa.components.input.UITextInput
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.*
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import club.sk1er.elementa.font.DefaultFonts
import club.sk1er.mods.core.universal.UChat
import java.awt.Color

class KtTestGui : WindowScreen() {
    private val block by UIBlock(Color.BLACK).constrain {
        x = CenterConstraint()
        height = 12.pixels()
        width = 120.pixels()
        y = 40.percent()
    } childOf window

    private val textInput by UITextInput("hello").constrain {
        x = CenterConstraint()
        width = 90.percent()
        y = CenterConstraint()
        fontProvider = DefaultFonts.VANILLA_FONT_RENDERER
    } childOf block

    private val block2 by UIBlock(Color.BLACK).constrain {
        x = CenterConstraint()
        y = SiblingConstraint(10f)
        width = 120.pixels()
        height = 12.pixels()
    } childOf window

    private val textInput2 by UIPasswordInput(placeholder = "hello2").constrain {
        x = CenterConstraint()
        width = 90.percent()
        y = CenterConstraint()
        fontProvider = DefaultFonts.VANILLA_FONT_RENDERER
    } childOf block2

    init {
        block.onMouseClick { textInput.grabWindowFocus() }
        block2.onMouseClick { textInput2.grabWindowFocus() }
    }

    init {
        UIBlock(Color.PINK).constrain {
            x = CenterConstraint()
            height = 20.pixels()
            width = 80.pixels()
            y = CenterConstraint()
        }.onMouseClick {
            UChat.chat("b1: ${textInput.getText()}")
            UChat.chat("b2: ${textInput2.getText()}")
        } childOf window

        Inspector(window) childOf window
    }

    private fun animImgSmall(img: UIImage) {
        img.animate {
            setWidthAnimation(Animations.LINEAR, 7f, 5.pixels(), 0.5f)
            setHeightAnimation(Animations.LINEAR, 7f, 5.pixels(), 0.5f)
            onComplete {
                animImgBig(img)
            }
        }
    }

    private fun animImgBig(img: UIImage) {
        img.animate {
            setWidthAnimation(Animations.LINEAR, 7f, RelativeConstraint(0.25f), 0.5f)
            setHeightAnimation(Animations.LINEAR, 7f, RelativeConstraint(0.25f), 0.5f)
            onComplete {
                animImgSmall(img)
            }
        }
    }

    //#if MC<11500
    override fun doesGuiPauseGame() = false
    //#endif

    private class Slider(text: String, yConstraint: PositionConstraint) : UIContainer() {
        private val slider = UICircle(5f, Color(0, 0, 0, 255))


        init {
            slider
                .setY(CenterConstraint())
                .onMouseEnter {
                    hover()
                }.onMouseLeave {
                    unhover()
                }.onMouseClick {
                    println("radius: " + slider.getRadius())
                }
            this
                .setX(PixelConstraint(15f))
                .setY(yConstraint)
                .setWidth(FillConstraint() + (-15).pixels())
                .setHeight(PixelConstraint(30f))
                .addChildren(
                    UIText(text),
                    UIBlock(Color(64, 64, 64, 255))
                        .setY(12.pixels())
                        .setWidth(FillConstraint())
                        .setHeight(5.pixels())
                        .addChild(slider)
                )
        }

        fun hover() {
            slider.animate {
                setRadiusAnimation(Animations.OUT_EXP, 1f, 10.pixels())
            }
        }

        fun unhover() {
            slider.animate {
                setRadiusAnimation(Animations.OUT_EXP, 1f, 5.pixels())
            }
        }
    }

    private class Notify(textString: String, titleString: String? = null, var delay: Float = 3f) :
        UIBlock(Color(0, 0, 0, 200)) {
        private val timer = UIBlock(Color(0, 170, 255, 255)).constrain {
            y = 0.pixels(true)
            height = 7.pixels()
        } childOf this

        private val glow = UIShape(Color(255, 255, 255, 30))
        private val text = UIWrappedText(textString, true).constrain {
            x = 2.pixels()
            y = SiblingConstraint() + 6.pixels()
            width = FillConstraint() - 2.pixels()
        }
        private val title = UIText(titleString ?: "").constrain {
            x = 2.pixels()
            y = 5.pixels()
            textScale = 1.5f.pixels()
        }

        private val timerAnimation = timer.makeAnimation()
        private var clicked = false

        init {
            glow childOf this
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels()))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))
            glow.addVertex(UIPoint(0.pixels(true), 0.pixels(true)))

            if (titleString != null) {
                title childOf this
            }

            text childOf this

            constrain {
                x = 0.pixels(alignOpposite = true, alignOutside = true)
                y = 5.pixels(true)
                width = 155.pixels()
                height = ChildBasedSizeConstraint() + 11.pixels()
            }.onMouseEnter {
                if (clicked) return@onMouseEnter
                timerAnimation.width.pauseIfSupported()
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 50).toConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 20.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 2f, 24.pixels(true))
                }
            }.onMouseLeave {
                if (clicked) return@onMouseLeave
                timerAnimation.width.resumeIfSupported()
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 1f, Color(255, 255, 255, 30).toConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 10.pixels(true))
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 2f, 14.pixels(true))
                }
            }.onMouseClick {
                clicked = true
                glow.animate {
                    setColorAnimation(Animations.OUT_EXP, 0.5f, Color(100, 100, 100, 255).toConstraint())
                }
                glow.getVertices()[1].animate {
                    setXAnimation(Animations.OUT_EXP, 0.5f, 0.pixels())
                    onComplete {
                        hide()
                    }
                }
                glow.getVertices()[2].animate {
                    setXAnimation(Animations.OUT_EXP, 1f, 0.pixels())
                }
            }

            animateBeforeHide {
                setXAnimation(Animations.IN_EXP, 0.5f, 0.pixels(alignOpposite = true, alignOutside = true), 0.5f)
            }

            animateAfterUnhide {
                timer.setWidth(0.pixels())
                clicked = false

                setXAnimation(Animations.OUT_EXP, 0.5f, 5.pixels(true))
                onComplete {
                    animateTimer()
                }
            }
        }

        fun setText(text: String, title: String? = null) {
            this.text.setText(text)
            this.title.setText(title ?: "")

            if (!children.contains(this.title)) this.title childOf this
        }

        private fun animateTimer() {
            glow.getVertices()[1].animate {
                setXAnimation(Animations.OUT_EXP, 1f, 10.pixels(true))
            }
            glow.getVertices()[2].animate {
                setXAnimation(Animations.OUT_EXP, 2f, 14.pixels(true))
            }
            timerAnimation
                .setWidthAnimation(Animations.LINEAR, delay, RelativeConstraint(), 0.5f)
                .begin()
                .onComplete {
                    hide()
                }
        }
    }
}