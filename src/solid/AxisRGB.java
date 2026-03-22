package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class AxisRGB extends Solid {
    public AxisRGB() {
        double len = 1.5;
        vertexBuffer.add(new Vertex(0, 0, 0));
        vertexBuffer.add(new Vertex(len, 0, 0, new Col(0xff0000), new Vec2D()));
        vertexBuffer.add(new Vertex(0, len, 0, new Col(0x00ff00), new Vec2D()));
        vertexBuffer.add(new Vertex(0, 0, len, new Col(0x0000ff), new Vec2D()));

        addIndices(0, 1);
        addIndices(0, 2);
        addIndices(0, 3);

        partBuffer.add(new Part(TopologyType.LINES, 0, 3));
    }
}
