package plugins.gradationConversions;

import java.util.HashMap;

public class GradationConversionsFactory {
    HashMap<String, Class> conversionsMap;
    {
        conversionsMap = new HashMap();
        conversionsMap.put("Negative", Negative.class);
        conversionsMap.put("Logarithm", Logarithm.class);
        conversionsMap.put("Power Function", PowerFunction.class);
    }

    public HashMap<String, Class> getConversionsMap() {
        return conversionsMap;
    }

    public GradationConversion createImageOperation(String name) {
        GradationConversion gradationConversion = null;
        Class c = conversionsMap.get(name);

        try {
            gradationConversion = (GradationConversion) c.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return gradationConversion;
    }
}
