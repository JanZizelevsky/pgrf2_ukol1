package renderer;

import objectdata.Part;
import objectdata.Vertex;
import rasterize.LineRasterizer;
import rasterize.TriangleRasterizer;
import solid.Solid;
import transforms.Mat4;
import transforms.Point3D;
import util.Clip;

import java.util.List;
import java.util.Optional;

public class RendererSolid {
    private LineRasterizer lineRasterizer;
    private TriangleRasterizer triangleRasterizer;

    public RendererSolid(LineRasterizer lineRasterizer, TriangleRasterizer triangleRasterizer) {
        this.lineRasterizer = lineRasterizer;
        this.triangleRasterizer = triangleRasterizer;
    }

    private Optional<Vertex> toNDC(Vertex vertex, Mat4 mvp) {
        Point3D pClip = vertex.getPosition().mul(mvp);
        double w = pClip.getW();
        if (w <= 0) return Optional.empty();
        double x = pClip.getX() / w;
        double y = pClip.getY() / w;
        double z = pClip.getZ() / w;
        return Optional.of(new Vertex(new Point3D(x, y, z), vertex.getColor(), vertex.getUv(), vertex.getNormal(), vertex.getWorldPosition()));
    }

    private Vertex toScreen(Vertex ndc, int width, int height) {
        double x = (ndc.getX() + 1.0) * width / 2.0;
        double y = (1.0 - ndc.getY()) * height / 2.0;
        return new Vertex(new Point3D(x, y, ndc.getZ()), ndc.getColor(), ndc.getUv(), ndc.getNormal(), ndc.getWorldPosition());
    }

    public void render(Solid solid, Mat4 model, Mat4 view, Mat4 projection, int width, int height, boolean wireframe) {
        Mat4 mvp = model.mul(view).mul(projection);
        for (Part part : solid.getPartBuffer()) {
            switch (part.getTopologyType()) {
                case LINES:
                    renderLines(solid, part, model, mvp, width, height);
                    break;
                case TRIANGLES:
                    if (wireframe)
                        renderTrianglesWireframe(solid, part, model, mvp, width, height);
                    else
                        renderTriangles(solid, part, model, mvp, width, height);
                    break;
            }
        }
    }

    private Vertex transformToWorld(Vertex v, Mat4 model) {
        transforms.Vec3D normal = v.getNormal();
        transforms.Vec3D worldNormal = new transforms.Point3D(normal.getX(), normal.getY(), normal.getZ(), 0).mul(model).ignoreW().normalized().orElse(new transforms.Vec3D(0,0,1));
        return new Vertex(v.getPosition(), v.getColor(), v.getUv(), worldNormal, v.getPosition().mul(model));
    }

    private void renderLines(Solid solid, Part part, Mat4 model, Mat4 mvp, int width, int height) {
        int index = part.getStartIndex();
        for (int i = 0; i < part.getCount(); i++) {
            Vertex aModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);
            Vertex bModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);

            Optional<Vertex> aOpt = toNDC(aModel, mvp);
            Optional<Vertex> bOpt = toNDC(bModel, mvp);
            if (aOpt.isEmpty() || bOpt.isEmpty()) continue;

            Vertex aNdc = aOpt.get(), bNdc = bOpt.get();
            if (!Clip.fastClipLine(aNdc, bNdc)) continue;

            var clipped = Clip.clipLineZ(aNdc, bNdc);
            if (clipped.isEmpty()) continue;

            Vertex[] ab = clipped.get();
            lineRasterizer.setColor(ab[1].getColor());
            lineRasterizer.rasterize(toScreen(ab[0], width, height), toScreen(ab[1], width, height));
        }
    }

    private void renderTriangles(Solid solid, Part part, Mat4 model, Mat4 mvp, int width, int height) {
        int index = part.getStartIndex();
        for (int i = 0; i < part.getCount(); i++) {
            Vertex aModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);
            Vertex bModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);
            Vertex cModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);

            Optional<Vertex> aOpt = toNDC(aModel, mvp);
            Optional<Vertex> bOpt = toNDC(bModel, mvp);
            Optional<Vertex> cOpt = toNDC(cModel, mvp);
            if (aOpt.isEmpty() || bOpt.isEmpty() || cOpt.isEmpty()) continue;

            Vertex a = aOpt.get(), b = bOpt.get(), c = cOpt.get();
            if (!Clip.fastClipTriangle(a, b, c)) continue;

            List<Vertex[]> clipped = Clip.clipTriangleZ(a, b, c);
            for (Vertex[] tri : clipped) {
                Vertex aScr = toScreen(tri[0], width, height);
                Vertex bScr = toScreen(tri[1], width, height);
                Vertex cScr = toScreen(tri[2], width, height);
                triangleRasterizer.rasterize(aScr, bScr, cScr, solid.getShader());
            }
        }
    }

    private void renderTrianglesWireframe(Solid solid, Part part, Mat4 model, Mat4 mvp, int width, int height) {
        int index = part.getStartIndex();
        for (int i = 0; i < part.getCount(); i++) {
            Vertex aModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);
            Vertex bModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);
            Vertex cModel = transformToWorld(solid.getVertexBuffer().get(solid.getIndexBuffer().get(index++)), model);

            Optional<Vertex> aOpt = toNDC(aModel, mvp);
            Optional<Vertex> bOpt = toNDC(bModel, mvp);
            Optional<Vertex> cOpt = toNDC(cModel, mvp);
            if (aOpt.isEmpty() || bOpt.isEmpty() || cOpt.isEmpty()) continue;

            Vertex a = aOpt.get(), b = bOpt.get(), c = cOpt.get();
            if (!Clip.fastClipTriangle(a, b, c)) continue;

            Vertex aScr = toScreen(a, width, height);
            Vertex bScr = toScreen(b, width, height);
            Vertex cScr = toScreen(c, width, height);
            lineRasterizer.rasterize(aScr, bScr);
            lineRasterizer.rasterize(bScr, cScr);
            lineRasterizer.rasterize(cScr, aScr);
        }
    }

    public void setLineRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void setTriangleRasterizer(TriangleRasterizer triangleRasterizer) {
        this.triangleRasterizer = triangleRasterizer;
    }
}
