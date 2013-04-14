package image;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 12.04.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
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