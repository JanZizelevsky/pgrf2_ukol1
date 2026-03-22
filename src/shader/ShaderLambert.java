package shader;

import objectdata.Vertex;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

public class ShaderLambert implements Shader {
    private Point3D lightPos;
    private Shader baseShader;
    private Col lightColor;

    public ShaderLambert(Point3D lightPos, Shader baseShader) {
        this(lightPos, baseShader, new Col(0xffffff));
    }

    public ShaderLambert(Point3D lightPos, Shader baseShader, Col lightColor) {
        this.lightPos = lightPos;
        this.baseShader = baseShader;
        this.lightColor = lightColor;
    }

    @Override
    public Col getColor(Vertex pixel) {
        Col baseColor = baseShader.getColor(pixel);
        Vec3D n = pixel.getNormal().normalized().orElse(new Vec3D(0, 0, 1));
        Vec3D l = lightPos.add(pixel.getWorldPosition().mul(-1)).ignoreW().normalized().orElse(new Vec3D(0, 0, 1));

        double diffuse = Math.max(0, n.dot(l));
        double ambient = 0.2;

        double r = Math.min(1.0, ambient + lightColor.getR() * diffuse);
        double g = Math.min(1.0, ambient + lightColor.getG() * diffuse);
        double b = Math.min(1.0, ambient + lightColor.getB() * diffuse);

        return new Col(
                baseColor.getR() * r,
                baseColor.getG() * g,
                baseColor.getB() * b);
    }
}
