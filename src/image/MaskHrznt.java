package image;

/**
 * Created by IntelliJ IDEA.
 * User: root
 * Date: 12.04.12
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
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