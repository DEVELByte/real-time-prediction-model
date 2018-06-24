package in.develbyte.ml.controller;

import in.develbyte.ml.service.interfaces.PredictorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
public class PridictionController {

    @Autowired
    private PredictorService predictorService;

    @GetMapping("/predict/wine_quality")
    public String predictWineQuality(@RequestParam String fixedAcidity,
            @RequestParam String volatileAcidity,
            @RequestParam String density,
            @RequestParam String citricAcid)
    {
        Map<String, String> input = new HashMap<String, String>();
        input.put("fixed_acidity", fixedAcidity);
        input.put("volatile_acidity", volatileAcidity);
        input.put("density", density);
        input.put("citric_acid", citricAcid);

        return predictorService.predict(input);
    }
}
