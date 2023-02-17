package org.example.inference.service;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.repository.zoo.ZooModel;
import org.example.inference.socket.WebSocketConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

@Service
public class InferenceService {
    private static final Logger logger = LoggerFactory.getLogger(InferenceService.class);
    private final SimpMessagingTemplate websocket;

    @Autowired
    private ZooModel inferenceModel;

    @Value("${labelsArray}")
    private String[] labelArray;


    public InferenceService(SimpMessagingTemplate websocket){
        this.websocket = websocket;
    }

    public void predictionSocket(InputStream is){
        logger.info("Predict Socket");
        HashMap<String, Float> prediction = new HashMap<>();
        float[][] outcome = this.predict(is);

        for (int i = 0; i < outcome.length; i++) {

            for(int j=0; j<outcome[i].length; j++) {
                //logger.info("Prediction outcome: " + outcome[0]);
                logger.info("########Values at outcome["+i+"]["+j+"] is: " + outcome[i][j]);
                String label  = labelArray[i];
                logger.info("#######Label at index ["+i+"] : "+ label);
                prediction.put(label, outcome[i][j]);
            }
        }
        logger.info("Predictions Hashmap: ", prediction);
        websocket.convertAndSend(WebSocketConfiguration.MESSAGE_PREFIX+"/prediction/response", prediction);
    }


    public float[][] predict(InputStream is) {

        try (Predictor<Image, float[][]> predictor = inferenceModel.newPredictor()) {
            Image img = ImageFactory.getInstance().fromInputStream( is);
            float[][] outcome = predictor.predict(img);
            return outcome ;
        }
        catch (IOException ioe){
            logger.error("Failed to get the image input stream "+ ioe.getMessage());
            throw new RuntimeException("Failed to get the image input stream ", ioe);
        }
        catch (Exception e) {
            logger.error("Failed to predict the outcome"+ e.getMessage());
            throw new RuntimeException("Failed to predict the outcome", e);
        }
    }
}
