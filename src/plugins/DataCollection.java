package plugins;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public enum DataCollection {
    INSTANCE;

    private LinkedList<Integer> line;
    private TreeMap<String, ImageProcessor> imageProcessorTreeMap;
    private ImagePlus imageOriginal, imageResult;
    private ImageStack stack;
    private Integer[] luminances, gradients, laplasians, lowerCompletions, status, shedLabels, prevShedLabels, wshPoints, nodeLabels;
    private ArrayList<Integer> waterShedPoints;
    private int maxLuminance = 255;

    public void addtoHistory(ImagePlus imagePlus) {
        if (imageProcessorTreeMap == null) {
            imageProcessorTreeMap = new TreeMap<>();
            stack = new ImageStack(imagePlus.getWidth(), imagePlus.getHeight());
        }
        imageProcessorTreeMap.put(imageProcessorTreeMap.size() + "_" + imagePlus.getTitle(), imagePlus.getProcessor().duplicate());
    }

    public ImageStack getHistoryStack() {
        if (imageProcessorTreeMap == null) {
            return new ImageStack();
        }
        stack.trim();
        for (Map.Entry<String, ImageProcessor> entry : imageProcessorTreeMap.entrySet()) {
            ImageProcessor impr = entry.getValue();
            String title = entry.getKey();
            stack.addSlice(title, impr);
        }
        return stack;
    }

    public void clearHistory() {
        if (imageProcessorTreeMap != null) {
            imageProcessorTreeMap = null;
        }
        if (stack != null) {
            stack.trim();
            stack = null;
        }
    }

    public ImagePlus getImageOriginal() {
        return imageOriginal;
    }

    public void setImageOriginal(ImagePlus imageOriginal) {
        this.imageOriginal = imageOriginal;
    }

    public ImagePlus getImageResult() {
        return imageResult;
    }

    public void setImageResult(ImagePlus imageResult) {
        this.imageResult = imageResult;
    }

    public void newLuminances(int length) {
        luminances = new Integer[length];
        for (int i = 0; i < length; i++) {
            luminances[i] = -1;
        }
    }

    public void newGradient(int length) {
        gradients = new Integer[length];
        for (int i = 0; i < length; i++) {
            gradients[i] = -1;
        }
    }

    public void newLaplasian(int length) {
        laplasians = new Integer[length];
        for (int i = 0; i < length; i++) {
            laplasians[i] = -1;
        }
    }

    public void newLowerCompletions(int length) {
        lowerCompletions = new Integer[length];
        for (int i = 0; i < length; i++) {
            lowerCompletions[i] = -1;
        }
    }

    public void newStatus(int length) {
        status = new Integer[length];
        for (int i = 0; i < length; i++) {
            status[i] = -1;
        }

    }

    public void newShedLabels(int length) {
        shedLabels = new Integer[length];
        for (int i = 0; i < length; i++) {
            shedLabels[i] = -1;
        }
    }

    public void newPrevShedLabels() {
        prevShedLabels = shedLabels.clone();
    }

    public void newWshPoints(int length) {
        wshPoints = new Integer[length];
        for (int i = 0; i < length; i++) {
            wshPoints[i] = -1;
        }
    }

    public void newWaterShedPoints() {
        waterShedPoints = new ArrayList<>();
    }

    public void newNodeLabels(int length) {
        nodeLabels = new Integer[length];
        for (int i = 0; i < length; i++) {
            nodeLabels[i] = -1;
        }
    }

    public void newLine() {
        line = new LinkedList<>();
    }

    public void setLuminance(int id, Integer value) {
        luminances[id] = value;
    }

    public void setGradient(int id, Integer value) {
        gradients[id] = value;
    }

    public void setLaplasian(int id, Integer value) {
        laplasians[id] = value;
    }

    public void setLowerCompletion(int id, Integer value) {
        lowerCompletions[id] = value;
    }

    public void setStatus(int id, Integer value) {
        status[id] = value;
    }

    public void setShedLabel(int id, Integer value) {
        shedLabels[id] = value;
    }

    public void setWshPoint(int id, Integer value) {
        wshPoints[id] = value;
    }

    public void addWaterShedPoint(int id) {
        waterShedPoints.add(id);
    }

    public void setNodeLabel(int id, Integer value) {
        nodeLabels[id] = value;
    }

    public void setMaxLuminance(int value) {
        maxLuminance = value;
    }

    public void setLine(LinkedList<Integer> _line) {
        line = new LinkedList<>(_line);
    }

    public void addPointToLine(Integer id) {
        line.add(id);
    }


    public Integer getLuminance(int id) {
        return luminances[id];
    }

    public Integer getGradient(int id) {
        return gradients[id];
    }

    public Integer getLaplasian(int id) {
        return laplasians[id];
    }

    public Integer getLowerCompletion(int id) {
        return lowerCompletions[id];
    }

    public Integer getStatus(int id) {
        return status[id];
    }

    public Integer getShedLabel(int id) {
        return shedLabels[id];
    }

    public Integer getPrevShedLabel(int id) {
        return prevShedLabels[id];
    }

    public Integer getWshPoint(int id) {
        return wshPoints[id];
    }

    public Integer getNodeLabel(int id) {
        return nodeLabels[id];
    }

    public int getMaxLuminance() {
        return maxLuminance;
    }


    public Integer[] getLuminances() {
        return luminances;
    }

    public Integer[] getGradients() {
        return gradients;
    }

    public Integer[] getLaplasians() {
        return laplasians;
    }

    public Integer[] getLowerCompletions() {
        return lowerCompletions;
    }

    public Integer[] getStatuses() {
        return status;
    }

    public Integer[] getShedLabels() {
        return shedLabels;
    }

    public Integer[] getWshPoints() {
        return wshPoints;
    }

    public ArrayList<Integer> getWaterShedPoints() {
        return waterShedPoints;
    }

    public Integer[] getNodeLabels() {
        return nodeLabels;
    }

    public LinkedList<Integer> getLine() {
        return line;
    }

    public void removeWatershedPoint(Integer id) {
        wshPoints[id] = 0;
        waterShedPoints.remove(id);
    }
}

