package workers;

import ij.process.ImageProcessor;

import java.util.*;

public class MassiveWorker {
    private ArrayList<Integer> ids = new ArrayList<Integer>();
    private TreeMap <Integer, ArrayList<Integer>> map = new TreeMap<Integer, ArrayList<Integer>>();
    private int max;
    private int min;

    public void sort(Integer[] luminance, Integer[] lowercompletion) {
        Integer[] id = new Integer[luminance.length];
        for(int i = 0; i < luminance.length; i++) {
            id[i] = i;
        }
        Arrays.sort(id, new MyDifficultComparator(luminance, lowercompletion));
        findExtremums(luminance);
        ids.clear();
        Collections.addAll(ids, id);
    }

    public void sort(ArrayList<Integer> idA, Integer[] array) {
        Integer[] id = new Integer[idA.size()];
        for(int i = 0; i < idA.size(); i++)
            id[i] = idA.get(i);
        Arrays.sort(id, new MyComparator(array));

        ids.clear();
        Collections.addAll(ids, id);
    }

    public void sort(ImageProcessor ip) {

        int[] pixels_ = (int[])ip.getPixels();
        Integer[] pixels = new Integer[pixels_.length];
        int i = 0;
        for (int value : pixels_) {
            pixels[i++] = Integer.valueOf(value);
        }
        Integer[] id = new Integer[pixels.length];
        for(i = 0; i < pixels.length; i++)
            id[i] = i;
        Arrays.sort(id, new MyComparator(pixels));
        findExtremums(pixels);
        generateMap(pixels);

        ids.clear();
        Collections.addAll(ids, id);
    }

    public void sort(Integer[] array) {
        Integer[] id = new Integer[array.length];
        for(int i = 0; i < array.length; i++)
            id[i] = i;
        Arrays.sort(id, new MyComparator(array));
        findExtremums(array);
        generateMap(array);

        ids.clear();
        Collections.addAll(ids, id);


    }

    public void scale(Integer[] array, int maxNew) {
        findExtremums(array);
        int i = 0;
        while (i < array.length) {
            try {
                array[i] = (array[i] - min)*maxNew/(max - min);
            } catch (ArithmeticException e) {
                System.out.println("( " + max + ", " + min + ") array[i] = " + array[i]);
                //e.printStackTrace();
                array[i] = 0;
            }
            i++;
        }
    }

    public void scale(Integer[] array) {
        findExtremums(array);
        int i = 0;
        while (i < array.length) {
            try {
                array[i] = (array[i] - min)*255/(max - min);
            } catch (ArithmeticException e) {
                System.out.println("( " + max + ", " + min + ") array[i] = " + array[i]);
                //e.printStackTrace();
                array[i] = 0;
            }
            i++;
        }
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public ArrayList<Integer> getIds() {
        return ids;
    }

    public TreeMap<Integer, ArrayList<Integer>> getMap() {
        return map;
    }

    private void generateMap(Integer[] array) {
        map.clear();
        for( int i = 0; i < array.length; i++) {
            if(map.containsKey(array[i])) {
                ArrayList<Integer> ar = map.get(array[i]);
                ar.add(i);
                map.put(array[i], ar);
            }
            else {
                ArrayList<Integer> ar =new ArrayList<Integer>();
                ar.add(i);
                map.put(array[i], ar);
            }
        }
    }

    public void findExtremums(Integer[] array) {
        ArrayList<Integer> ar = new ArrayList<>();
        Collections.addAll(ar, array);
        max = Collections.max(ar);
        min = Collections.min(ar);
    }

    private class MyComparator implements Comparator<Integer> {
        Integer[] values;

        private MyComparator(Integer[] array) {
            values = array;
        }
        public int compare(Integer i, Integer j) {
            if(values[i] > values[j])
                return -1;
            else if(values[i].equals(values[j]))
                return 0;
            else if(values[i] < values[j])
                return 1;
            else
                return i.compareTo(j);
        }
    }

    private class MyDifficultComparator implements Comparator<Integer> {
        Integer[] luminance;
        Integer[] lowerCompletion;

        private MyDifficultComparator(Integer[] array1, Integer[] array2) {
            luminance = array1;
            lowerCompletion = array2;
        }
        public int compare(Integer i, Integer j) {
            if(luminance[i] > luminance[j]) {
                return -1;
            }
            if(luminance[i] < luminance[j]) {
                return 1;
            }
            if(lowerCompletion[i] > lowerCompletion[j]) {
                return -1;
            }
            if(lowerCompletion[i] < lowerCompletion[j]) {
                return 1;
            }
            return 0;
        }
    }
}
