package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Cube extends Solid {
    public Cube() {
        double s = 0.5;
        Col[] colors = { new Col(0xff0000), new Col(0x00ff00), new Col(0x0000ff),
                new Col(0xffff00), new Col(0xff00ff), new Col(0x00ffff) };

        vertexBuffer.add(new Vertex(-s, -s, -s, colors[0], new Vec2D(0, 0)));
        vertexBuffer.add(new Vertex(s, -s, -s, colors[0], new Vec2D(1, 0)));
        vertexBuffer.add(new Vertex(s, s, -s, colors[0], new Vec2D(1, 1)));
        vertexBuffer.add(new Vertex(-s, s, -s, colors[0], new Vec2D(0, 1)));
        vertexBuffer.add(new Vertex(-s, -s, s, colors[1], new Vec2D(0, 0)));
        vertexBuffer.add(new Vertex(s, -s, s, colors[1], new Vec2D(1, 0)));
        vertexBuffer.add(new Vertex(s, s, s, colors[1], new Vec2D(1, 1)));
        vertexBuffer.add(new Vertex(-s, s, s, colors[1], new Vec2D(0, 1)));

        addIndices(0, 1, 2, 0, 2, 3);
        addIndices(5, 4, 7, 5, 7, 6);
        addIndices(4, 0, 3, 4, 3, 7);
        addIndices(1, 5, 6, 1, 6, 2);
        addIndices(3, 2, 6, 3, 6, 7);
        addIndices(4, 5, 1, 4, 1, 0);
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, 12));
    }
}
