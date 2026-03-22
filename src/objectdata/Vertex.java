package objectdata;

import transforms.Col;
import transforms.Point3D;
import transforms.Vec2D;
import transforms.Vec3D;

public class Vertex implements Vectorizable<Vertex> {
    private final Point3D position;
    private final Col color;
    private final Vec2D uv;
    private final Vec3D normal;
    private final Point3D worldPosition;

    public Vertex(Point3D position, Col color, Vec2D uv, Vec3D normal, Point3D worldPosition) {
        this.position = position;
        this.color = color;
        this.uv = uv;
        this.normal = normal;
        this.worldPosition = worldPosition;
    }

    public Vertex(Point3D position, Col color, Vec2D uv, Vec3D normal) {
        this(position, color, uv, normal, new Point3D(0, 0, 0));
    }

    public Vertex(Point3D position, Col color, Vec2D uv) {
        this(position, color, uv, new Vec3D(0, 0, 1));
    }

    public Vertex(Point3D position) {
        this(position, new Col(1., 1., 1.), new Vec2D(), new Vec3D(0, 0, 1));
    }

    public Vertex(double x, double y, double z) {
        this(new Point3D(x, y, z), new Col(1., 1., 1.), new Vec2D(), new Vec3D(0, 0, 1));
    }

    public Vertex(double x, double y, double z, Col color, Vec2D uv) {
        this(new Point3D(x, y, z), color, uv, new Vec3D(0, 0, 1));
    }

    public Vertex(double x, double y, double z, Col color, Vec2D uv, Vec3D normal) {
        this(new Point3D(x, y, z), color, uv, normal);
    }

    public Point3D getPosition() {
        return position;
    }

    public Col getColor() {
        return color;
    }

    public Vec2D getUv() {
        return uv;
    }

    public Vec3D getNormal() {
        return normal;
    }

    public Point3D getWorldPosition() {
        return worldPosition;
    }

    public double getX() {
        return this.position.getX();
    }

    public double getY() {
        return this.position.getY();
    }

    public double getZ() {
        return this.position.getZ();
    }

    @Override
    public Vertex mul(double d) {
        return new Vertex(this.position.mul(d), this.color.mul(d), this.uv.mul(d), this.normal.mul(d),
                this.worldPosition.mul(d));
    }

    @Override
    public Vertex add(Vertex v) {
        return new Vertex(this.position.add(v.getPosition()), this.color.add(v.getColor()), this.uv.add(v.getUv()),
                this.normal.add(v.getNormal()), this.worldPosition.add(v.getWorldPosition()));
    }
}
