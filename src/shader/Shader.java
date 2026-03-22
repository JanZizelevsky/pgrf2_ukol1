package shader;

import objectdata.Vertex;
import transforms.Col;

public interface Shader {
    Col getColor(Vertex pixel);
}
