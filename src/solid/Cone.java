package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Cone extends Solid {
    public Cone() {
        this(0.5, 1, 16);
    }

    public Cone(double radius, double height, int segments) {
        double h = height / 2;
        vertexBuffer.add(new Vertex(0, h, 0, new Col(0xff0066), new Vec2D(0.5, 0)));
        vertexBuffer.add(new Vertex(0, -h, 0, new Col(0xff0066), new Vec2D(0.5, 0.5)));
        for (int i = 0; i <= segments; i++) {
            double t = 2 * Math.PI * i / segments;
            double x = radius * Math.cos(t);
            double z = radius * Math.sin(t);
            double u = (double) i / segments;
            vertexBuffer.add(new Vertex(x, -h, z, new Col(0xaa00ff), new Vec2D(u, 1)));
        }
        int base = 2;
        for (int i = 0; i < segments; i++) {
            addIndices(0, base + i, base + i + 1);
            addIndices(1, base + i + 1, base + i);
        }
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, segments * 2));
    }
}
