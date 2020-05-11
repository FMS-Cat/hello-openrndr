import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.draw.*
import org.openrndr.extra.fx.blur.GaussianBloom
import org.openrndr.ffmpeg.VideoWriter
import org.openrndr.math.Vector2
import org.openrndr.math.Vector3
import org.openrndr.math.map
import org.openrndr.math.transforms.transform

fun main() = application {
    configure {
        width = 1280
        height = 720
    }

    program {
        val resolution = Vector2(width.toDouble(), height.toDouble())
        val aspect = resolution.x / resolution.y

        val image = loadImage("data/images/pm5544.png")
        val bloom = GaussianBloom()

        val videoWriter = VideoWriter.create().size(width, height).output("output.mp4").start()

        val target = renderTarget(width, height) {
            // It crashes on my end :(
            // exits with 0xC0000005 @ 1280x720
            // exits with 0xC0000374 @ 480x480
//            colorBuffer(ColorFormat.RGBa, ColorType.FLOAT32)
            colorBuffer()
            depthBuffer()
        }

        val wet = colorBuffer(width, height, 1.0, ColorFormat.RGBa, ColorType.FLOAT32)

        extend {
            drawer.isolatedWithTarget(target) {
                clear(ColorRGBa.BLACK)
            }

            drawer.isolated {
                drawer.lookAt(
                        Vector3(0.0, 0.0, 5.0),
                        Vector3(0.0, 0.0, 0.0),
                        Vector3.UNIT_Y
                )
                drawer.perspective(45.0, aspect, 0.1, 100.0)

                for (i in 0..4) {
                    val x = map(
                            0.0,
                            4.0,
                            -2.5,
                            2.5,
                            i.toDouble()
                    )
                    val theta = 90.0 * seconds - map(
                            0.0,
                            4.0,
                            0.0,
                            90.0,
                            i.toDouble()
                    )

                    drawer.isolatedWithTarget(target) {
                        model = transform {
                            translate(x, 0.0, 0.0)
                            rotate(Vector3.UNIT_X, theta)
                        }

                        shadeStyle = textureStyle(image)
                        stroke = null
                        rectangle(-0.5, -0.5, 1.0, 1.0)
                    }
                }
            }

            drawer.isolated {
                val dry = target.colorBuffer(0)
                bloom.apply(dry, wet)
                image(wet)
            }

            drawer.isolatedWithTarget(target) {
                image(wet)
            }

            if (frameCount <= 100) {
                videoWriter.frame(target.colorBuffer(0))

                if (frameCount == 100) {
                    videoWriter.stop()
                }
            }
        }
    }
}