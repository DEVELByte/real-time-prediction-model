package in.develbyte.ml.service;

import com.google.common.collect.BiMap;
import in.develbyte.ml.service.interfaces.PredictorService;
import org.dmg.pmml.Entity;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.jpmml.model.PMMLUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class WineQualityPredictor implements PredictorService {

    private Evaluator evaluator;

    @Autowired
    public WineQualityPredictor(@Value("${model.wine}") String storedModel) {
        evaluator = this.loadModel(storedModel);
    }

    private Map<FieldName, FieldValue> inputFieldMapping(Map<String, String> input) {
        Map<FieldName, FieldValue> arguments = new HashMap<>();
        List<InputField> inputFieldList = evaluator.getInputFields();
        for (InputField inputField : inputFieldList) {
            FieldName fieldName = inputField.getName();
            FieldValue fieldValue = inputField.prepare(input.get(fieldName.getValue()));
            arguments.put(fieldName, fieldValue);
        }
        return arguments;
    }

    private List<String> outputFiledMapping(Map<FieldName, ?> results) {
        List<String> response = new ArrayList<>();
        List<TargetField> targetFieldsList = evaluator.getTargetFields();
        for (TargetField targetField : targetFieldsList) {
            FieldName targetFieldName = targetField.getName();
            Object targetFieldValue = results.get(targetFieldName);
            if (targetFieldValue instanceof Computable) {
                Computable computable = (Computable) targetFieldValue;

                Object unboxedTargetFieldValue = computable.getResult();
                System.out.println(targetFieldName.getValue() + " : " + unboxedTargetFieldValue);
                response.add(targetFieldName.getValue() + " : " + unboxedTargetFieldValue);
            } else if (targetFieldValue instanceof HasEntityId) {
                HasEntityId hasEntityId = (HasEntityId) targetFieldValue;
                HasEntityRegistry<?> hasEntityRegistry = (HasEntityRegistry<?>) evaluator;
                BiMap<String, ? extends Entity> entities = hasEntityRegistry.getEntityRegistry();
                Entity winner = entities.get(hasEntityId.getEntityId());

                if (targetFieldValue instanceof HasProbability) {
                    HasProbability hasProbability = (HasProbability) targetFieldValue;
                    Double winnerProbability = hasProbability.getProbability(winner.getId());
                    System.out.println(targetFieldName.getValue() + " : " + winnerProbability);
                    response.add(targetFieldName.getValue() + " : " + winnerProbability);
                }
            }
        }
        return response;
    }

    @Override
    public String predict(Map<String, String> input) {
        Map<FieldName, FieldValue> arguments = inputFieldMapping(input);
        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        List<String> response = outputFiledMapping(results);
        return Arrays.toString(response.toArray(new String[0]));
    }

    private InputStream getStoredModelStream(String storedModel) {
        try {
            ClassPathResource resource = new ClassPathResource(storedModel);
            return resource.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private PMML getPMMl(String storedModel) {
        PMML pmml = null;
        try {
            pmml = PMMLUtil.unmarshal(getStoredModelStream(storedModel));
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JAXBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pmml;
    }

    private Evaluator loadModel(String storedModel) {
        PMML pmml = getPMMl(storedModel);
        if (pmml != null) {
            ModelEvaluatorFactory modelEvalFactory = ModelEvaluatorFactory.newInstance();
            Evaluator evaluator = modelEvalFactory.newModelEvaluator(pmml);
            evaluator.verify();
            return evaluator;
        }
        return null;
    }
}
