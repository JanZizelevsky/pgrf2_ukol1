package solid;

import objectdata.Part;
import objectdata.TopologyType;
import objectdata.Vertex;
import transforms.Col;
import transforms.Vec2D;

public class Sphere extends Solid {
    public Sphere() {
        this(new transforms.Point3D(0, 0, 0), 0.5, 16, 12);
    }

    public Sphere(double radius, int slices, int stacks) {
        this(new transforms.Point3D(0, 0, 0), radius, slices, stacks);
    }

    public Sphere(transforms.Point3D center, double radius, int slices, int stacks) {
        double cx = center.getX();
        double cy = center.getY();
        double cz = center.getZ();

        for (int j = 0; j <= stacks; j++) {
            double phi = Math.PI * j / stacks;
            double y = radius * Math.cos(phi) + cy;
            double r = radius * Math.sin(phi);
            for (int i = 0; i <= slices; i++) {
                double theta = 2 * Math.PI * i / slices;
                double x = r * Math.cos(theta) + cx;
                double z = r * Math.sin(theta) + cz;
                double u = (double) i / slices;
                double v = (double) j / stacks;
                vertexBuffer.add(new Vertex(x, y, z, new Col(0x00aaff), new Vec2D(u, v),
                        new transforms.Vec3D(x - cx, y - cy, z - cz).normalized().get()));
            }
        }
        for (int j = 0; j < stacks; j++) {
            for (int i = 0; i < slices; i++) {
                int a = j * (slices + 1) + i;
                int b = a + slices + 1;
                addIndices(a, b, a + 1);
                addIndices(a + 1, b, b + 1);
            }
        }
        partBuffer.add(new Part(TopologyType.TRIANGLES, 0, slices * stacks * 2));
    }
}
