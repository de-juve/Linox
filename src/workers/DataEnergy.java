package workers;


public class DataEnergy {
    private double[] coefficients;
    private double u_k, u_p, u_dp, u_sk, u_c;
    private int borderUp, borderDown;
    private double energyValue;


    public DataEnergy(int _borderUp, int _borderDown, double U_k, double U_sk, double U_p, double U_dp, double U_c) {
        coefficients = new double[4];
        borderUp = _borderUp;
        borderDown = _borderDown;
        u_k = U_k;
        u_p = U_p;
        u_dp = U_dp;
        u_sk = U_sk;
        u_c = U_c;
    }

    public DataEnergy(DataEnergy data) {
        coefficients = new double[data.getCoefficients().length];
        System.arraycopy(data.getCoefficients(), 0, coefficients, 0, data.getCoefficients().length);
        u_k = data.getU_k();
        u_p = data.getU_p();
        u_dp = data.getU_dp();
        u_sk = data.getU_sk();
        u_c = data.getU_c();
        energyValue = data.getEnergyValue();
        borderUp = data.getBorderUp();
        borderDown = data.getBorderDown();
    }

    public double getCoefficient(int id) {
        return coefficients[id];
    }

    public void setCoefficient(int id, double val) {
        coefficients[id] = val;
    }

    public void move(int id, int dx) {
        if (((coefficients[id] + dx) <= borderUp) && ((coefficients[id] + dx) >= borderDown))
            coefficients[id] += dx;
    }


    public boolean compareCoefficients(double[] b) {
        return coefficients[0] != b[0] || coefficients[1] != b[1] || coefficients[2] != b[2] || coefficients[3] != b[3];
    }

    public boolean checkCoefficients() {
        return coefficients[2] > coefficients[0] && coefficients[2] > coefficients[1] && coefficients[3] > coefficients[0] && coefficients[3] > coefficients[1];
    }


    public double getEnergyValue() {
        energyValue = coefficients[0] * u_p + coefficients[1] * u_dp + coefficients[2] * u_k + coefficients[2] * u_sk + coefficients[3] * u_c;
        return energyValue;
    }

    public double[] getCoefficients() {
        return coefficients;
    }

    public double getU_k() {
        return u_k;
    }

    public double getU_p() {
        return u_p;
    }

    public double getU_dp() {
        return u_dp;
    }

    public double getU_sk() {
        return u_sk;
    }

    public double getU_c() {
        return u_c;
    }

    public int getBorderUp() {
        return borderUp;
    }

    public int getBorderDown() {
        return borderDown;
    }
}
