package solid;

import objectdata.Part;
import objectdata.Vertex;
import shader.Shader;
import shader.ShaderConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {
    protected final List<Vertex> vertexBuffer = new ArrayList<>();
    protected final List<Integer> indexBuffer = new ArrayList<>();
    protected final List<Part> partBuffer = new ArrayList<>();
    protected Shader shader = new ShaderConstant();

    public List<Vertex> getVertexBuffer() {
        return vertexBuffer;
    }

    public List<Integer> getIndexBuffer() {
        return indexBuffer;
    }

    public List<Part> getPartBuffer() {
        return partBuffer;
    }

    public void addIndices(Integer... indices) {
        indexBuffer.addAll(Arrays.asList(indices));
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }
}
