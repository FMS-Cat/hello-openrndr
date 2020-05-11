import org.openrndr.color.ColorRGBa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.ShadeStyle
import org.openrndr.extra.parameters.ColorParameter
import org.openrndr.extra.parameters.Description
import org.openrndr.extra.parameters.DoubleParameter
import org.openrndr.math.Vector2

@Description("Texture style")
class TextureStyle(
    texture: ColorBuffer) : ShadeStyle() {

    var texture: ColorBuffer by Parameter()

    init {
        this.texture = texture

        fragmentTransform = """
            vec2 texCoord = c_boundsPosition.xy;
            texCoord.y = 1.0 - texCoord.y;
            vec2 size = textureSize(p_texture, 0);
            x_fill = texture(p_texture, texCoord);
        """

        parameter("texture", texture)
    }
}

fun textureStyle(
    texture: ColorBuffer
) : ShadeStyle {
    return TextureStyle(texture)
}