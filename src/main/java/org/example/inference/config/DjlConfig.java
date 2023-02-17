package org.example.inference.config;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.util.NDImageUtils;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class    DjlConfig {

    //Custom Translator
    public static final class InferenceTranslator implements Translator<Image, float[][]> {

        private static final Logger logger = LoggerFactory.getLogger(InferenceTranslator.class);

        @Value("${zoomodel.model.urls}")
        private String modelUrls;

        @Override
        public NDList processInput(TranslatorContext ctx, Image input) {

            NDManager manager = ctx.getNDManager();
            //convert Prediction object variables to NDArray
            NDArray array = input.toNDArray(manager, Image.Flag.COLOR);

            logger.info("Input NDArray: "+array.toString());
            NDList list = new NDList(array);
            logger.info("Input NDList: "+list.toString());
            return new NDList(NDImageUtils.toTensor(array));
        }

        @Override
        public float[][] processOutput(TranslatorContext ctx, NDList list) {
            NDList result = new NDList();
            logger.info("Output NDArray: " +list.singletonOrThrow().toString());
            long numOutputs = list.singletonOrThrow().getShape().get(0);
            logger.info("Number of Outputs for the model: " + numOutputs);
            for (int i = 0; i < numOutputs; i++) {
                logger.info("Predicted Outcome : "+ i + " " + list.singletonOrThrow().get(i));
                result.add(list.singletonOrThrow().get(i));
            }
            //logger.info("Label 1: ", labels[0]);
            //convert prediction result to a float[][] i.e [[0.67966]]
            return result.stream().map(NDArray::toFloatArray).toArray(float[][]::new);
        }

        @Override
        public Batchifier getBatchifier() {
            return Batchifier.STACK;
        }

        @Bean
        public ZooModel pimaInferenceModel() throws Exception {
            Criteria<Image, float[][]> criteria =
                    Criteria.builder()
                            .setTypes(Image.class, float[][].class)
                            .optModelUrls(modelUrls)
                            .optEngine("TensorFlow")
                            .optOption("Tags", "serve")
                            .optOption("SignatureDefKey", "serving_default")
                            .optTranslator(new InferenceTranslator())
                            .optProgress(new ProgressBar())
                            .build();
            return ModelZoo.loadModel(criteria);
        }
    }

}
