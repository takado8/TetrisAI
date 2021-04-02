package tetris.ai.neural;

public class SimpleNeuralNet {
    private final int[] shape;
    private final double[] weights;
    private int layerIndex = 1;
    private int weightsIndex = 0;

    public SimpleNeuralNet(int[] shape, double[] weights) {
        this.shape = shape;
        this.weights = weights;

    }

    public double[] feedNet(double[] inputs) {
        double[] outputs = null;
        int layersNumber = shape.length - 1;
        for (int i = 0; i < layersNumber; i++) {
            outputs = feedLayer(inputs);
            inputs = outputs;
        }
        layerIndex = 1;
        weightsIndex = 0;
        return outputs;
    }

    private double[] feedLayer(double[] inputs) {
        int numberOfNeuronsInLayer = shape[layerIndex++];
        double[] outputs = new double[numberOfNeuronsInLayer];

        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = 0;
            for (double input : inputs) {
                outputs[i] += input * weights[weightsIndex++];
            }
        }
        return outputs;
    }
}
