package workers;

import java.util.ArrayList;
import java.util.HashSet;


public class GetterNeighboures {
    ArrayList<Integer> neighboures = new ArrayList<Integer>();
    ArrayList<Integer> ids = new ArrayList<Integer>();
    Integer[] array;
    int width;
    int height;

    public GetterNeighboures(Integer[] array) {
        this.array = array;
    }

    public ArrayList<Integer> getElements(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        neighboures.clear();
        ids.clear();
        neighboures.add(getElement(x - 1, y - 1));
        neighboures.add(getElement(x - 1 + 1, y - 1));
        neighboures.add(getElement(x - 1 + 2, y - 1));
        neighboures.add(getElement(x - 1, y - 1 + 1));
        neighboures.add(getElement(x - 1 + 1, y - 1 + 1));
        neighboures.add(getElement(x - 1 + 2, y - 1 + 1));
        neighboures.add(getElement(x - 1, y - 1 + 2));
        neighboures.add(getElement(x - 1 + 1, y - 1 + 2));
        neighboures.add(getElement(x - 1 + 2, y - 1 + 2));
        return neighboures;
    }

    public ArrayList<Integer> getElements(int i, int width, int height) {
        int x = (int) (i % width);
        int y = (int) (i / width);
        return getElements(x, y, width, height);
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public ArrayList<Integer> getNeighboures() {
        return neighboures;
    }

    public ArrayList<Integer> getIds(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        ids.clear();
        ids.add(getId(x - 1, y - 1));
        ids.add(getId(x - 1 + 1, y - 1));
        ids.add(getId(x - 1 + 2, y - 1));
        ids.add(getId(x - 1, y - 1 + 1));
        ids.add(getId(x - 1 + 1, y - 1 + 1));
        ids.add(getId(x - 1 + 2, y - 1 + 1));
        ids.add(getId(x - 1, y - 1 + 2));
        ids.add(getId(x - 1 + 1, y - 1 + 2));
        ids.add(getId(x - 1 + 2, y - 1 + 2));
        return ids;

    }

    public ArrayList<Integer> getShedIds(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        ids.clear();
        ids.add(getId(x - 1, y - 1));
        ids.add(getId(x - 1 + 1, y - 1));
        ids.add(getId(x - 1 + 2, y - 1));
        ids.add(getId(x - 1, y - 1 + 1));

        return ids;

    }

    public ArrayList<Integer> getIdEnlargeds(int i, int width, int height) {
        int x = (int) (i % width);
        int y = (int) (i / width);
        return getIdEnlargeds(x, y, width, height);
    }

    public ArrayList<Integer> getIdEnlargeds(int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        ids.clear();
        ids.add(getId(x - 2, y - 2));
        ids.add(getId(x - 2 + 1, y - 2));
        ids.add(getId(x - 2 + 2, y - 2));
        ids.add(getId(x - 2 + 3, y - 2));
        ids.add(getId(x - 2 + 4, y - 2));

        ids.add(getId(x - 2, y - 2 + 1));
        ids.add(getId(x - 2 + 1, y - 2 + 1));
        ids.add(getId(x - 2 + 2, y - 2 + 1));
        ids.add(getId(x - 2 + 3, y - 2 + 1));
        ids.add(getId(x - 2 + 4, y - 2 + 1));

        ids.add(getId(x - 2, y - 2 + 2));
        ids.add(getId(x - 2 + 1, y - 2 + 2));
        ids.add(getId(x - 2 + 2, y - 2 + 2));
        ids.add(getId(x - 2 + 3, y - 2 + 2));
        ids.add(getId(x - 2 + 4, y - 2 + 2));

        ids.add(getId(x - 2, y - 2 + 3));
        ids.add(getId(x - 2 + 1, y - 2 + 3));
        ids.add(getId(x - 2 + 2, y - 2 + 3));
        ids.add(getId(x - 2 + 3, y - 2 + 3));
        ids.add(getId(x - 2 + 4, y - 2 + 3));

        ids.add(getId(x - 2, y - 2 + 4));
        ids.add(getId(x - 2 + 1, y - 2 + 4));
        ids.add(getId(x - 2 + 2, y - 2 + 4));
        ids.add(getId(x - 2 + 3, y - 2 + 4));
        ids.add(getId(x - 2 + 4, y - 2 + 4));


        return ids;

    }

    public ArrayList<Integer> getIds(int i, int width, int height) {
        int x = i % width;
        int y = i / width;
        getIds(x, y, width, height);
        return ids;
    }


    public ArrayList<Integer> getIdEqualElements(int i, int width, int height, Integer[] potentialLevel) {
        ArrayList<Integer> ids = getCorrectNeighbouresIds(i, width, height, potentialLevel);
        if (ids.size() > 1) {
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.addAll(ids);
            ids.clear();
            ids.addAll(hs);
        }
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (array[i].equals(array[id]) && i != id) {
                res.add(id);
            }
        }
        return res;
    }

    public ArrayList<Integer> getNeighbouresIds(Integer i, int width, int height, Integer[] potentionalLevel) {
        ArrayList<Integer> ids = getIds(i, width, height);

        if (ids.size() > 1) {
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.addAll(ids);
            hs.remove(i);
            ids.clear();
            ids.addAll(hs);
        }
        return ids;
    }

    public ArrayList<Integer> getNeighbouresIdsE(Integer i, int width, int height, Integer[] potentionalLevel) {
        ArrayList<Integer> ids = getIdEnlargeds(i, width, height);

        if (ids.size() > 1) {
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.addAll(ids);
            hs.remove(i);
            ids.clear();
            ids.addAll(hs);
        }
        return ids;
    }

    public ArrayList<Integer> getCorrectNeighbouresIds(Integer i, int width, int height, Integer[] potentionalLevel) {
        ArrayList<Integer> ids = getIds(i, width, height);
        ArrayList<Integer> res = new ArrayList<Integer>();
        for (Integer id : ids) {
            if (isDiagonalNeighboure(i, id)) {
                if (diagonalNeighboureCondition(i, id, potentionalLevel)) {
                    res.add(id);
                }
            } else {
                res.add(id);
            }
        }
        if (res.size() > 1) {
            HashSet<Integer> hs = new HashSet<Integer>();
            hs.addAll(res);
            hs.remove(i);
            res.clear();
            res.addAll(hs);
        }
        return res;
    }

    public ArrayList<Integer> getShedNeighbouresIds(Integer i, int width, int height, Integer[] potentionalLevel) {
        int x = i % width;
        int y = i / width;
        ArrayList<Integer> ids = getIds(x, y, width, height);
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : ids) {
            if (isDiagonalNeighboure(i, id)) {
                if (diagonalNeighboureCondition(i, id, potentionalLevel)) {
                    if (potentionalLevel[i].equals(potentionalLevel[id]) && !i.equals(id))
                        res.add(id);
                }
            } else {
                if (potentionalLevel[i].equals(potentionalLevel[id]) && !i.equals(id))
                    res.add(id);
            }
        }
        if (res.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(res);
            hs.remove(i);
            res.clear();
            res.addAll(hs);
        }
        return res;
    }

    public ArrayList<Integer> getShedNeighbouresIds2(Integer i, int width, int height, Integer[] potentionalLevel) {
        int x = i % width;
        int y = i / width;
        ArrayList<Integer> ids = getShedIds(x, y, width, height);//Ids(x, y, width, height);
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : ids) {
            if (isDiagonalNeighboure(i, id)) {
                if (diagonalNeighboureCondition(i, id, potentionalLevel)) {
                    if (potentionalLevel[i].equals(potentionalLevel[id]) && i > id)//!i.equals(id))
                        res.add(id);
                }
            } else {
                if (potentionalLevel[i].equals(potentionalLevel[id]) && i > id)//!i.equals(id))
                    res.add(id);
            }
        }
        if (res.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(res);
            hs.remove(i);
            res.clear();
            res.addAll(hs);
        }
        return res;
    }

    public ArrayList<Integer> getCorrectNeighbouresIdsEnlarge(int i, int width, int height, Integer[] potentionalLevel) {
        int x = i % width;
        int y = i / width;
        ArrayList<Integer> ids = getIdEnlargeds(x, y, width, height);
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : ids) {
            if (isDiagonalNeighboure(i, id)) {
                if (diagonalNeighboureCondition(i, id, potentionalLevel)) {
                    res.add(id);
                }
            } else {
                res.add(id);
            }
        }
        return res;
    }

    public ArrayList<Integer> getWatershedIds(Integer i, int width, int height, Integer[] watershed) {
        ArrayList<Integer> ids = getIds(i, width, height);
        if (ids.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(ids);
            ids.clear();
            ids.addAll(hs);
        }
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : ids) {
            if (watershed[id] == 255) {
                res.add(id);
            }
        }
        res.remove(i);
        if (res.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(res);
            res.clear();
            res.addAll(hs);
        }
        return res;
    }

    public ArrayList<Integer> getWatershedIds(Integer i, int width, int height, Integer[] watershed, boolean[] crosPoints) {
        ArrayList<Integer> ids = getIds(i, width, height);
        if (ids.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(ids);
            ids.clear();
            ids.addAll(hs);
        }
        ArrayList<Integer> res = new ArrayList<>();
        for (Integer id : ids) {
            if (watershed[id] == 255) {
                if (!isDiagonalNeighboure(i, id))
                    res.add(0, id);
                else {
                    if (!crosPoints[i]) {
                        if (wdiagonalNeihbCondition(i, id, crosPoints))
                            res.add(id);
                    } else
                        res.add(id);
                }
            }
        }
        res.remove(i);
        if (res.size() > 1) {
            HashSet<Integer> hs = new HashSet<>();
            hs.addAll(res);
            res.clear();
            res.addAll(hs);
        }
        return res;
    }

    private boolean isDiagonalNeighboure(int p, int n) {
        int xp = p % width;
        int yp = p / width;
        int xn = n % width;
        int yn = n / width;
        return xp != xn && yp != yn;
    }


    private boolean diagonalNeighboureCondition(int p, int n, Integer[] potentionalLevel) {
        int xp = p % width;
        int yn = n / width;
        int p1 = getId(xp, yn);
        int yp = p / width;
        int xn = n % width;
        int p2 = getId(xn, yp);

        return !((potentionalLevel[p1] > potentionalLevel[p] || potentionalLevel[p1] > potentionalLevel[n]) &&
                (potentionalLevel[p2] > potentionalLevel[p] || potentionalLevel[p2] > potentionalLevel[n]));
    }

    private boolean wdiagonalNeihbCondition(int p, int n, boolean[] crosPoints) {
        int xp = p % width;
        int yn = n / width;
        int x1 = getId(xp, yn);
        int yp = p / width;
        int xn = n % width;
        int x2 = getId(xn, yp);

        return !crosPoints[x1] && !crosPoints[x2];
    }


    private int getElement(int x, int y) {
        int i = getId(x, y);
        ids.add(i);
        return array[i];
    }

    private int getId(int x, int y) {
        while (x < 0)
            x++;
        while (y < 0)
            y++;
        while (x >= width)
            x--;
        while (y >= height)
            y--;
        int offset, i;
        offset = y * width;
        i = offset + x;
        if (i < 0 || i >= array.length) {

            i = getId(x, y);
        }
        return i;
    }
}
