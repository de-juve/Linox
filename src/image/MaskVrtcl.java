package image;

public class MaskVrtcl extends Mask{
    public MaskVrtcl()
    {
        super();
        weight = new int[]{
                3, 0, -3,
                10, 0, -10,
                3, 0, -3
        };
        countSumWeights();
    }
}