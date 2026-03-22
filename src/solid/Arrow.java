package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Arrow extends Solid {

    public Arrow() {
        vertexBuffer.add(new Vertex(-0.5, 0, 0));
        vertexBuffer.add(new Vertex(0.5, 0, 0));
        vertexBuffer.add(new Vertex(0.3, 0.15, 0, new Col(0xff0000), new Vec2D(1, 0))); // v2
        vertexBuffer.add(new Vertex(0.3, -0.15, 0, new Col(0x00ff00), new Vec2D(0, 0.5))); // v3
        vertexBuffer.add(new Vertex(0.5, 0, 0, new Col(0x0000ff), new Vec2D(1, 1))); // v4

        addIndices(0, 1);
        addIndices(4, 3, 2);

        partBuffer.add(new Part(TopologyType.LINES, 0, 1));
        partBuffer.add(new Part(TopologyType.TRIANGLES, 2, 1));
    }
}
