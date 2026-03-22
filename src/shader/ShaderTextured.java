package shader;

import objectdata.Vertex;
import transforms.Col;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ShaderTextured implements Shader {
    private BufferedImage texture;

    public ShaderTextured(String filePath) {
        try {
            texture = ImageIO.read(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            texture = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            texture.setRGB(0, 0, 0xFF00FF);
        }
    }

    @Override
    public Col getColor(Vertex pixel) {
        double u = pixel.getUv().getX();
        double v = pixel.getUv().getY();

        u = u - Math.floor(u);
        v = v - Math.floor(v);

        int x = (int) (u * (texture.getWidth() - 1));
        int y = (int) ((1 - v) * (texture.getHeight() - 1));

        int rgb = texture.getRGB(Math.min(Math.max(x, 0), texture.getWidth() - 1),
                Math.min(Math.max(y, 0), texture.getHeight() - 1));
        return new Col(rgb);
    }
}
