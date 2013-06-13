package plugins.roadGraph;

import java.util.ArrayList;


public class CrossMasks {
    protected int[] weight;
    protected int sumWeight;

    public CrossMasks(int[] _weight)
    {
        super();
        weight =_weight;
        countSumWeights();
    }

    public Integer R(int[] watersheds)
    {
        Integer result = 0;
        for(int i = 0; i < watersheds.length; i++)
        {
            result +=   weight[i]*watersheds[i];
        }
        return result/sumWeight;
    }

    private void countSumWeights()
    {
        for (int aWeight : weight) sumWeight += Math.abs(aWeight);
    }
}