package image;

public class MaskDgnl extends Mask{
    public MaskDgnl()
    {
        super();
        weight = new int[] {
                0, 3, 10,
                -3, 0, 3,
                -10, -3, 0
        };
        countSumWeights();
    }
}