package org.example.inference.controller;

import org.example.inference.payload.ApiResponse;
import org.example.inference.service.InferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(ModelInferenceController.class);

    @Autowired
    private InferenceService inferenceService;

    @MessageMapping("/prediction/socket")
    public ResponseEntity<?> predictionSocket(@RequestParam("file") MultipartFile file) throws Exception{
        logger.info("Prediction Socket endpoint called");
        inferenceService.predictionSocket(file.getInputStream());
        return ResponseEntity.ok().body(new ApiResponse(true, "Submitted file"+file.getName()+" for prediction"));
    }
}
