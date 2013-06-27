package workers;

import ij.process.ImageProcessor;
import plugins.DataCollection;
import road.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class PixelsMentor {

    public static ArrayList defineNeighboursIdsWithSameValueLuminance(int id, ImageProcessor ip) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, ip);
        for (Integer nid : neighbouresIds) {
            if (DataCollection.INSTANCE.getLuminance(id).equals(DataCollection.INSTANCE.getLuminance(nid))) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList defineNeighboursIdsWithLowerValue(int id, Integer[] values, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (values[id] > values[nid]) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList defineNeighboursIdsWithLowerValueDeviation(int id, int deviation, Integer[] values, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (Math.abs(values[id] - values[nid]) <= deviation) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList defineNeighboursIdsWidthDiagonalCondition(int id, Integer[] values, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (isDiagonalNeighboure(id, nid, width)) {
                if (diagonalNeighboureCondition(id, nid, values, width, height)) {
                    if (values[id].equals(values[nid]) && id != nid)
                        resultArray.add(nid);
                }
            } else {
                if (values[id].equals(values[nid]) && id != nid)
                    resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList<Integer> defineNeighboursIdsWidthDiagonalCondition(Integer id, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (isDiagonalNeighboure(id, nid, width)) {
                if (diagonalNeighboureCondition(id, nid, DataCollection.INSTANCE.getLuminances(), width, height) && id != nid) {
                    resultArray.add(nid);
                }
            }  else if ( id != nid) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    private static boolean isDiagonalNeighboure(int p, int n, int width) {
        int xp = p % width;
        int yp = p / width;
        int xn = n % width;
        int yn = n / width;
        return xp != xn && yp != yn;
    }


    private static boolean diagonalNeighboureCondition(int p, int n, Integer[] values, int width, int height) {
        int xp = p % width;
        int yn = n / width;
        int p1 = getId(xp, yn, width, height);
        int yp = p / width;
        int xn = n % width;
        int p2 = getId(xn, yp, width, height);

        return !((values[p1] > values[p] || values[p1] > values[n]) &&
                (values[p2] > values[p] || values[p2] > values[n]));
    }


    public static ArrayList defineNeighboursIdsWithSameValue(int id, Integer[] values, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (values[id].equals(values[nid])) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }

    public static ArrayList defineNeighboursIdsWithSameValueDeviation(int id, int deviation, Integer[] values, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighbouresIds = defineNeighboursIds(id, width, height);
        for (Integer nid : neighbouresIds) {
            if (Math.abs(values[id] - values[nid]) < deviation) {
                resultArray.add(nid);
            }
        }
        return resultArray;
    }


    public static ArrayList defineNeighboursIds(int id, int width, int height) {
        return defineNeighboursIds(id, 1, width, height);
    }

    public static ArrayList defineNeighboursIds(int id, ImageProcessor ip) {
        return defineNeighboursIds(id, ip.getWidth(), ip.getHeight());
    }

    public static ArrayList defineNeighboursIds(int id, int radius, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        int[] size = {width, height};
        int x = id % width;
        int y = id / width;

        for (int yy = y - radius; yy <= y + radius; yy++) {
            for (int xx = x - radius; xx <= x + radius; xx++) {
                if (xx == x && yy == y) {
                    continue;
                }
                resultArray.add(getId(xx, yy, size));
            }
        }
        if (resultArray.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(resultArray);
            hs.remove(-1);
            resultArray.clear();
            resultArray.addAll(hs);
        }
        return resultArray;
    }

    public static Integer defineNeighbourId(int id, Direction direction, int width, int height) {
        int[] size = {width, height};
        int i = direction.getNeighboureId(id, width);
        if (i < 0 || i > size[0] * size[1]) {
            i = -1;
        }
        return i;
    }

    public static ArrayList defineAllNeighboursIds(int id, int width, int height) {
        return defineAllNeighboursIds(id, 1, width, height);
    }

    public static ArrayList defineAllNeighboursIds(int id, int radius, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        int[] size = {width, height};
        int x = id % width;
        int y = id / width;

        for (int yy = y - radius; yy <= y + radius; yy++) {
            for (int xx = x - radius; xx <= x + radius; xx++) {
                resultArray.add(getId(xx, yy, size));
            }
        }

        Integer obj = -1;
        ArrayList<Integer> remove = new ArrayList<>(1);
        remove.add(obj);

        resultArray.removeAll(remove);

        return resultArray;
    }

    public static boolean isNeighboures(int id, int jd, int width) {
        int ix = id % width;
        int iy = id / width;
        int jx = jd % width;
        int jy = jd / width;

        if (Math.abs(ix - jx) <= 1 && Math.abs(iy - jy) <= 1) {
            return true;
        }
        return false;
    }

    public static int getId(int x, int y, int width, int height) {
        return getId(x, y, new int[]{width, height});
    }

    public static int getId(int x, int y, int[] size) {
        while (x < 0)
            x++;
        while (y < 0)
            y++;
        while (x >= size[0])
            x--;
        while (y >= size[1])
            y--;
        int offset, i;
        offset = y * size[0];
        i = offset + x;
        if (i < 0 || i > size[0] * size[1]) {
            i = -1;
        }
        return i;
    }

    public static int getX(int id, int width) {
        return id % width;
    }

    public static int getY(int id, int width) {
        return id / width;
    }

    public static ArrayList<Integer> getWatershedNeighbouresIds(Integer p, ArrayList<Integer> crpoints, int width, int height) {
        ArrayList<Integer> resultArray = new ArrayList<>();
        ArrayList<Integer> neighboures = defineAllNeighboursIds(p, 1, width, height);
        for (Integer n : neighboures) {
            if (DataCollection.INSTANCE.getWshPoint(n) == 255) {
                if (isDiagonalNeighboure(p, n, width)) {
                    int xp = p % width;
                    int yp = p / width;
                    int xn = n % width;
                    int yn = n / width;
                    int id1 = xp + yn * width;
                    int id2 = xn + yp * width;
                    if (!crpoints.contains(id1) && !crpoints.contains(id2)) {
                        resultArray.add(n);
                    }
                } else {
                    resultArray.add(n);
                }
            }
        }
        return resultArray;
    }
}
