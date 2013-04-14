package workers;

import java.awt.*;
import java.util.*;

public class ShedWorker {
    private volatile static ShedWorker worker;
    private int biggestLabel;
    private TreeMap<Integer, Shed> sheds;

    private ShedWorker() {
        sheds = new TreeMap<>();
        biggestLabel = 0;
    }

    public static ShedWorker getInstance() {
        if(worker == null) {
            synchronized (ShedWorker.class) {
                if(worker == null) {
                    worker = new ShedWorker();
                }
            }
        }
        return worker;
    }

    public void addShed(int label, int level, Color color) {
        if(sheds.containsKey(label))
            return;
        sheds.put(label, new Shed(label, level, color));
        if (biggestLabel <= label)
            biggestLabel = label;
    }


    public void addElementToShed(int label, Integer pixel) {
        sheds.get(label).addElement(pixel);
    }

    public boolean contain(int label, Integer pixel) {

        if(sheds.containsKey(label)) {
            Shed s = sheds.get(label);
            return s.contain(pixel);
        }
        return false;
    }

    public int getCanonical(int label) {
        if(sheds.containsKey(label)) {
            Shed s = sheds.get(label);
            return s.getCanonicalElement();
        }
        return -1;
    }

    public void setCanonical(int label, int canonical) {
        if(sheds.containsKey(label)) {
            Shed s = sheds.get(label);
            s.setCanonicalElement(canonical);
        }
    }

    public Color getShedColor(int label) {
        if(sheds.containsKey(label)) {
            return sheds.get(label).getColor();
        }
        return Color.black;
    }

    public ArrayList<Integer> getShedElements(int label) {
        if(sheds.containsKey(label)) {
            return sheds.get(label).getElements();
        }
        return null;
    }

    public Set<Integer> getLabels() {
        return sheds.keySet();
    }

    public void clear() {
        sheds.clear();
        biggestLabel = 0;
    }

    public boolean containShedWithLevel(int level) {
        for(Shed s : sheds.values()) {
            if(s.getLevel() == level)
                return true;
        }
        return false;
    }

    public ArrayList<Shed> getShedWithLevel(int level) {
        ArrayList<Shed> res = new ArrayList<Shed>();
        for(Shed s : sheds.values()) {
            if(s.getLevel() == level)
                res.add(s);
        }
        return res;
    }

    public void unionSheds(int label1, int label2) {
        if(sheds.containsKey(label1) && sheds.containsKey(label2)) {
            if(label1 == label2)
                return;
            sheds.get(label1).addElements(sheds.get(label2).getElements());
            sheds.get(label2).clear();
            sheds.remove(label2);
        }
    }

    /* public TreeMap<Integer, Shed> getSheds() {
        return sheds;
    }

    public Shed getShed(int label) {
        if (sheds.containsKey(label))
            return sheds.get(label);
        return null;
    }*/


    public class Shed {
        private ArrayList<Integer> elements;
        private int label;
        private Integer canonical;
        private Color color;
        private int level;

        public int getLevel() {
            return level;
        }

        public Shed() {
            elements = new ArrayList<>();
           // color = new HSLColor(Color.BLACK);
            canonical = -1;
        }

        public Shed(int label) {
            this.label = label;

            Random rand = new Random();

            //color = new HSLColor(label,90 +  rand.nextFloat() * 10, 50 + rand.nextFloat() * 10 );

            elements = new ArrayList<>();
            canonical = -1;
        }

        public Shed(int label, int level, Color color) {
            this.label = label;
            this.level = level;

            //Random rand = new Random();
           // float hue = 360 * label / DataCollection.INSTANCE.getLuminances().length;
            //float saturation, lightness;
            //saturation = lightness = 100 * label / DataCollection.INSTANCE.getLuminances().length;

            this.color = color;// new HSLColor(hue, 90 +   new Random().nextFloat() * 10, 50 +  new Random().nextFloat() * 10 );

            elements = new ArrayList<>();
            canonical = -1;
        }

        public void addElement(int pixel) {
            elements.add(pixel);
        }

        public boolean contain(int pixel) {
            return elements.contains(pixel);
        }

        public int getLabel() {

            return label;
        }

        public ArrayList<Integer> getElements() {
            return elements;
        }

        public Color getColor() {
            return color;
        }

        public void setCanonicalElement(int c) {
            if (elements.contains(c))
                canonical = c;
        }

        public int getCanonicalElement() {
            return canonical;
        }

        public void sortElements() {
            MassiveWorker worker = new MassiveWorker();
            Collections.sort(elements);
        }

        public void addElements(ArrayList<Integer> elements) {
            this.elements.addAll(elements);
            if(this.elements.size() > 1) {
                HashSet<Integer> hs = new HashSet<Integer>();
                hs.addAll(this.elements);
                this.elements.clear();
                this.elements.addAll(hs);
            }
        }

        public void clear() {
            this.elements.clear();
            this.label = -1;
            this.level = -1;
        }
    }
}