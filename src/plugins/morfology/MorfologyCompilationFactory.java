package plugins.morfology;

import image.*;

import java.util.HashMap;

public class MorfologyCompilationFactory {
    HashMap<String, Class> compilationMap;
    {
        compilationMap = new HashMap();
        compilationMap.put("Plus", ImageOperationPlus.class);
        compilationMap.put("Minus", ImageOperationMinus.class);
        compilationMap.put("Xor", ImageOperationXor.class);
        compilationMap.put("Min", ImageOperationMin.class);
        compilationMap.put("Max", ImageOperationMax.class);
        compilationMap.put("Smart", ImageOperationSmart.class);
        compilationMap.put("Smart minus Max", ImageOperationMaxMinusSmart.class);
    }

    public HashMap<String, Class> getCompilationMap() {
        return compilationMap;
    }

    public ImageOperation createImageOperation(String name) {
        ImageOperation imageOperation = null;
        Class c = compilationMap.get(name);

        try {
            imageOperation = (ImageOperation) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return imageOperation;
    }
}
