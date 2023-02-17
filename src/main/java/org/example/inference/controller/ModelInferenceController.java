package org.example.inference.controller;

import org.example.inference.payload.ApiResponse;
import org.example.inference.service.InferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
public class ModelInferenceController {
    private static final Logger logger = LoggerFactory.getLogger(ModelInferenceController.class);

    @Autowired
    private InferenceService inferenceService;


    @PostMapping("/prediction/socket/foliage")
    public ResponseEntity<?> foliagePredictionSocket(@RequestParam("file") MultipartFile file) {
        logger.info("Mango disease and pest Prediction Socket endpoint called");
        String message = "";
        try {
            inferenceService.predictionSocket(file.getInputStream());
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            logger.info(message);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(true, message));

        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            logger.error(message);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ApiResponse(false, message));
        }
    }
}
