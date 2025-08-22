package com.example.demo.services;

import com.example.demo.entity.RecognizeHistoryEntity;
import com.example.demo.model.RecognizerResponse;
import com.example.demo.model.VideoDetails;
import com.example.demo.util.Utils;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v2.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.example.demo.model.RecognizerError.INVALID_FILE;
import static com.example.demo.model.RecognizerError.LOW_BALANCE;
import static com.example.demo.util.GlobalConstants.*;

@Service
public class RecognizeService {

    private final CloudStorageService cloudService;
    private final MediaService mediaService;
    private final UserService userService;
    private final HistoryService historyService;

    @Autowired
    public RecognizeService(CloudStorageService cloudService, MediaService mediaService, UserService userService, HistoryService historyService) {
        this.historyService = historyService;
        this.cloudService = cloudService;
        this.mediaService = mediaService;
        this.userService = userService;
    }

    //    business method
    public RecognizerResponse recognizeByURL(String url, String username, int language) throws IOException, ExecutionException, InterruptedException {
        VideoDetails details = mediaService.getYoutubeVideoDetails(url);
        int audioDuration = details.getDuration();
        RecognizerResponse response = new RecognizerResponse();
        if (userService.hasEnoughAvailableSeconds(username, audioDuration)) {
            userService.decreaseAvailableSeconds(username, audioDuration);
            response = initRecognize(mediaService.downloadAudioByURL(url), language);
            historyService.addHistoryItem(new RecognizeHistoryEntity(username, details.getName(),
                    response.getRecognition(), new Date(), audioDuration));
            response.setUserInfo(userService.getUserInfo(username));
            return response;
        }
        response.setErrorMessage(LOW_BALANCE.getMessage());
        response.setUserInfo(userService.getUserInfo(username));
        return response;
    }

    //    business method
    public RecognizerResponse recognizeByMediaFile(MultipartFile file, String username, int language) throws IOException, ExecutionException, InterruptedException {
        int audioDuration;
        String downloadedFileName;
        RecognizerResponse response = new RecognizerResponse();
        try {
            downloadedFileName = mediaService.downloadUsersFile(file);
            audioDuration = mediaService.getMediaFileDuration(downloadedFileName);
        } catch (Exception e) {
            response.setErrorMessage(INVALID_FILE.getMessage());
            return response;
        }
        if (userService.hasEnoughAvailableSeconds(username, audioDuration)) {
            String localFileName;
            try {
                localFileName = mediaService.convertUsersFile(downloadedFileName);
            } catch (Exception e) {
                response.setErrorMessage(INVALID_FILE.getMessage());
                return response;
            }
            response = initRecognize(localFileName, language);
            userService.decreaseAvailableSeconds(username, audioDuration);
            historyService.addHistoryItem(new RecognizeHistoryEntity(username, file.getOriginalFilename(),
                    response.getRecognition(), new Date(), audioDuration));
            response.setUserInfo(userService.getUserInfo(username));
            return response;
        }
//        mediaService.removeMediaFile(localFileName);
        response.setErrorMessage(LOW_BALANCE.getMessage());
        response.setUserInfo(userService.getUserInfo(username));
        return response;
    }

    private RecognizerResponse initRecognize(String localFileName, int language) throws IOException, ExecutionException, InterruptedException {
        cloudService.uploadAudio(localFileName);
//        mediaService.removeMediaFile(localFileName);
        return recognize(CLOUD_STORAGE_PATH_PREFIX + BUCKET_NAME + "/" + localFileName, Utils.languageCodes.get(language));
    }


    private RecognizerResponse recognize(String filePath, String language) throws IOException, ExecutionException, InterruptedException {
        RecognizerResponse recognizerResponse = new RecognizerResponse();
        StringBuilder result = new StringBuilder();
        // Настроить клиента с учетными данными
        SpeechSettings speechSettings = SpeechSettings.newBuilder()
                .setEndpoint(API_ENDPOINT) // Укажите региональный эндпоинт
                .build();

        try (SpeechClient speechClient = SpeechClient.create(speechSettings)) {
            // Явно задаем конфигурацию распознавания
            RecognitionFeatures recognitionFeatures = RecognitionFeatures.newBuilder()
                    .setEnableAutomaticPunctuation(true)
//                    .setEnableWordTimeOffsets(true)
                    .setMaxAlternatives(2)
                    .build();

            RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
                    .setModel("chirp_2")
                    .addAllLanguageCodes(List.of(language))
                    .setAutoDecodingConfig(AutoDetectDecodingConfig.newBuilder().build())
                    .setFeatures(recognitionFeatures) // Используем recognitionFeatures для автоматической пунктуации
                    .build();

            BatchRecognizeFileMetadata files = BatchRecognizeFileMetadata.newBuilder().setUri(filePath).build();

            RecognitionOutputConfig outputConfig = RecognitionOutputConfig.newBuilder().setInlineResponseConfig(InlineOutputConfig.newBuilder().build())
                    .build();

            BatchRecognizeRequest request = BatchRecognizeRequest.newBuilder()
                    .setConfig(recognitionConfig)
                    .setRecognizer(RECOGNIZER)
                    .setRecognitionOutputConfig(outputConfig)
                    .addFiles(files)
                    .build();

            // Запускаем асинхронное распознавание
            OperationFuture<BatchRecognizeResponse, OperationMetadata> future = speechClient.batchRecognizeAsync(request);

            // Ожидаем завершения операции
            BatchRecognizeResponse response = future.get();
            Map<String, BatchRecognizeFileResult> resultsMap = response.getResultsMap();
            for (Map.Entry<String, BatchRecognizeFileResult> entry : resultsMap.entrySet()) {
                BatchRecognizeFileResult fileResult = entry.getValue();
                if (fileResult.hasInlineResult()) {
                    InlineResult inlineResult = fileResult.getInlineResult();
                    BatchRecognizeResults transcript = inlineResult.getTranscript();
                    List<SpeechRecognitionResult> recognitionResults = transcript.getResultsList();
                    for (SpeechRecognitionResult recognitionResult : recognitionResults) {
                        List<SpeechRecognitionAlternative> alternatives = recognitionResult.getAlternativesList();
                        for (SpeechRecognitionAlternative alternative : alternatives) {
                            result.append(alternative.getTranscript());
                        }
                    }
                } else if (fileResult.hasError()) {
                    System.err.println("Error: " + fileResult.getError().getMessage());
                } else {
                    System.out.println("No inline result or error found for file: " + entry.getKey());
                }
            }
        }
        recognizerResponse.setRecognition(result.toString());
        return recognizerResponse;
    }


}
