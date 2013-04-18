package image;

public class MaskLaplas extends Mask{
    public MaskLaplas()
    {
        super();
        weight = new int[]{
                1, 1, 1,
                1, -8, 1,
                1, 1, 1
        };
        countSumWeights();
    }
}
