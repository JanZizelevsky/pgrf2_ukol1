package shader;

import objectdata.Vertex;
import transforms.Col;

public class ShaderConstant implements Shader {
    private Col color;

    public ShaderConstant() {
        this.color = new Col(0xffff00);
    }

    public ShaderConstant(Col color) {
        this.color = color;
    }

    @Override
    public Col getColor(Vertex pixel) {
        return color;
    }
}
