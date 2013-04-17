package plugins.histogramChanging;

import java.util.HashMap;

public class HistogramChangingFactory {
    HashMap<String, Class> histogramChangingMap;
    {
        histogramChangingMap = new HashMap();
        histogramChangingMap.put("Equalisation", HistogramEqualisation.class);
    }

    public HashMap<String, Class> getHistogramChangingMap() {
        return histogramChangingMap;
    }

    public HistogramChanging createImageOperation(String name) {
        HistogramChanging histogramChanging = null;
        Class c = histogramChangingMap.get(name);

        try {
            histogramChanging = (HistogramChanging) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return histogramChanging;
    }
}
