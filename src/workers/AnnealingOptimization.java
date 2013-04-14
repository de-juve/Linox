package workers;

import java.util.Random;

public class AnnealingOptimization {

    public double optimize(double T0, double Tend, double decrement, DataEnergy energy)
    {
        Random r ;
        double minEnergy = 10000;
        double T = T0;
        double h;
        DataEnergy dataEnergy = new DataEnergy(energy);
        DataEnergy minDataEnergy = new DataEnergy(energy);
        boolean doIt = true;
        while (doIt)
        {
            for (int j = 0; j < 4; ++j)
            {
                r = new Random();
                double min = dataEnergy.getBorderDown();
                double max = dataEnergy.getBorderUp();
                double rand = min + (int)(r.nextDouble() * ((max - min) + 1));
                dataEnergy.setCoefficient(j, rand);
            }
            if (dataEnergy.checkCoefficients())
                doIt = false;
        }

        doIt = true;
        int i = 1;

        while (T > Tend)
        {
            if(dataEnergy.getEnergyValue() < minEnergy)
            {
                minDataEnergy = new DataEnergy(dataEnergy);
                minEnergy = minDataEnergy.getEnergyValue();
            }
            DataEnergy auxdata = new DataEnergy(dataEnergy);
            while(doIt)
            { //new point (coef)
                for (int coefId = 0; coefId < auxdata.getCoefficients().length; coefId++)
                {
                    newCoefficient(auxdata, coefId, T, i);
                }
                if (auxdata.checkCoefficients() && auxdata.compareCoefficients(dataEnergy.getCoefficients()))
                    doIt = false;
            }
            doIt = true;


            h = 1 / (1 + Math.exp((auxdata.getEnergyValue() - minEnergy) / T));
            r = new Random();
            if(r.nextDouble() < h)
            {
                dataEnergy = new DataEnergy(auxdata);
            }
            else
            {
                T = T0*Math.exp(-decrement*Math.pow(i, (double) (1)/((double) dataEnergy.getCoefficients().length)));
            }
            i++;

            /*if (dataEnergy.GetValue() < minEnergy)
            //if (dataEnergy.GetValue() > maxEnergy)
            {
                minDataEnergy = new DataEnergy(dataEnergy);
            }
            else
            {
                //! h = 1 / (1 + Math.Exp(-(maxEnergy - dataEnergy.GetValue()) / T));
                h = 1/(1 + Math.Exp((dataEnergy.GetValue() - minEnergy)/T));
                //! if(r.NextDouble() < h)
                if (r.NextDouble() > h)
                {
                    dataEnergy = new DataEnergy(minDataEnergy);
                }
                else
                {
                    minDataEnergy = new DataEnergy(dataEnergy);
                }
            }

            minEnergy = minDataEnergy.GetValue();
            T = T0*Math.Exp(-decrement*Math.Pow(i, (double) (1)/((double) dataEnergy.GetCoefCount())));
            ++i;*/
        }
        return minDataEnergy.getEnergyValue();
    }

    private void newCoefficient(DataEnergy dataEnergy, int id, double T, int i)
    {
        double z;
        int coef;
        boolean doIt = true;
        while (doIt)
        {
            Random r = new Random();
            double alpha = r.nextDouble();

            z = (Math.pow((1 + i / T), (2 * alpha - 1)) - 1) * T * Math.signum(alpha - 1 / 2);

            coef = (int) (dataEnergy.getCoefficient(id) + (int)((dataEnergy.getBorderUp() - dataEnergy.getBorderDown()) * z));

            if (coef <= dataEnergy.getBorderUp() && coef >= dataEnergy.getBorderDown())
            {
                dataEnergy.move(id, (int) ((dataEnergy.getBorderUp() - dataEnergy.getBorderDown()) * z));
                doIt = false;
            }
            /* else
            {
                i++;
                //NewCoef(dataEnergy, id, border, T, i + 1);
            }*/
        }

    }

}
