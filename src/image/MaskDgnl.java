package image;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 12.04.12
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
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