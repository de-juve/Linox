package image;

public class MaskHrznt extends Mask{
    public MaskHrznt()
    {
        super();
        weight = new int[]{
                3, 10, 3,
                0, 0, 0,
                -3, -10, -3
        };
        countSumWeights();
    }
}