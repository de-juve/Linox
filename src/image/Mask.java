package image;

import java.util.ArrayList;


public abstract class Mask {
    protected int[] weight;
    protected int radius;
    protected int sumWeight;


    public Integer R(ArrayList<Integer> luminances)
    {
        Integer result = 0;
        for(int i = 0; i < luminances.size(); i++)
        {
            result +=   weight[i]*luminances.get(i)/sumWeight;
        }
        return result;
        //return neighbours.Select((t, i) => weight[i]*t/sumWeight).Sum();
    }

    protected void countSumWeights()
    {
        for (int aWeight : weight) sumWeight += Math.abs(aWeight);
    }
}
