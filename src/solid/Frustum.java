package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Frustum extends Solid {
    public Frustum() {
        this(6, 0.5, 0.25, 1);
    }

    public Frustum(int n, double rBottom, double rTop, double height) {
        if (n < 3)
            n = 3;
        double h = height / 2;
        for (int i = 0; i <= n; i++) {
            double t = 2 * Math.PI * i / n;
            double cx = Math.cos(t);
            double cz = Math.sin(t);
            double u = (double) i / n;
            vertexBuffer.add(new Vertex(rBottom * cx, -h, rBottom * cz, new Col(0x0066ff), new Vec2D(u, 0)));
            vertexBuffer.add(new Vertex(rTop * cx, h, rTop * cz, new Col(0xff6600), new Vec2D(u, 1)));
        }
        for (int i = 0; i < n; i++) {
            int a = i * 2;
            addIndices(a, a + 1, a + 3, a, a + 3, a + 2);
        }
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, n * 2));
    }
}
