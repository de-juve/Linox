package workers;

public class PixelWorker {
    private int width;
    private int height;

    public PixelWorker(int w, int h) {
        width = w;
        height = h;
    }

    public boolean isNearBoundary(int i) {
        int x = (int)(i%width);
        int y = (int)(i/width);

        if(x == 1 || x >= (width-3) || y == 1 || y >= (height-3))
            return true;
        return false;
    }

    public boolean isBoundary(int i) {
        int x = (int)(i%width);
        int y = (int)(i/width);

        if(x == 0 || x == (width-1) || y == 0 || y == (height-1))
            return true;
        return false;
    }

    public boolean isNeighboures(int i, int j) {
        int xi = (int)(i%width);
        int yi = (int)(i/width);
        int xj = (int)((j)%width);
        int yj = (int)((j)/width);
        return Math.abs(xi - xj) <= 1 && Math.abs(yi - yj) <= 1;
    }


}
