package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Tetrahedron extends Solid {
    public Tetrahedron() {
        double s = 0.5;
        vertexBuffer.add(new Vertex(0, s, 0, new Col(0xff0000), new Vec2D(0.5, 0)));
        vertexBuffer.add(new Vertex(-s, -s, s, new Col(0x00ff00), new Vec2D(0, 1)));
        vertexBuffer.add(new Vertex(s, -s, s, new Col(0x0000ff), new Vec2D(1, 1)));
        vertexBuffer.add(new Vertex(0, -s, -s, new Col(0xffff00), new Vec2D(0.5, 1)));
        addIndices(0, 1, 2, 0, 2, 3, 0, 3, 1, 1, 3, 2);
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 4));
    }
}
