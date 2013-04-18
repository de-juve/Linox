package plugins.imageOperations;

import java.util.HashMap;

public class ImageOperationFactory {
    HashMap<String, Class> operationMap;
    {
        operationMap = new HashMap();
        operationMap.put("Plus", ImageOperationPlus.class);
        operationMap.put("Minus", ImageOperationMinus.class);
        operationMap.put("Xor", ImageOperationXor.class);
        operationMap.put("Min", ImageOperationMin.class);
        operationMap.put("Max", ImageOperationMax.class);
        operationMap.put("Smart", ImageOperationSmart.class);
        operationMap.put("Smart minus Max", ImageOperationMaxMinusSmart.class);
    }

    public HashMap<String, Class> getOperationMap() {
        return operationMap;
    }

    public ImageOperation createImageOperation(String name) {
        ImageOperation imageOperation = null;
        Class c = operationMap.get(name);

        try {
            imageOperation = (ImageOperation) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return imageOperation;
    }
}
