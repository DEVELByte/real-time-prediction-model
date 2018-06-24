package in.develbyte.ml.service.interfaces;

import java.util.Map;

public interface PredictorService {
    public String predict( Map<String, String> input);
}
