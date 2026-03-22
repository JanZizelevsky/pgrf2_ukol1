package shader;

import objectdata.Vertex;
import transforms.Col;

public class ShaderInterpolated implements Shader {
    @Override
    public Col getColor(Vertex pixel) {
        return pixel.getColor();
    }
}
