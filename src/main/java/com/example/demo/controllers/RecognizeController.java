package com.example.demo.controllers;

import com.example.demo.model.RecognizerResponse;
import com.example.demo.services.RecognizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
public class RecognizeController {

    private final RecognizeService recognizeService;

    @Autowired
    public RecognizeController(RecognizeService recognizeService) {
        this.recognizeService = recognizeService;
    }

    @GetMapping("/recognizer")
    public ModelAndView getRecognizerPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("recognizer");
        return modelAndView;
    }

    @GetMapping("/recognizeByURL")
    public RecognizerResponse recognizeByURL(@RequestParam String url,
                                             @RequestParam Integer language,
                                             @AuthenticationPrincipal UserDetails userDetails)
            throws IOException, ExecutionException, InterruptedException {
        return recognizeService.recognizeByURL(url, userDetails.getUsername(), language);
    }

    @PostMapping("/recognizeByMediaFile")
    public RecognizerResponse recognizeByMediaFile(@RequestParam("file") MultipartFile file,
                                                   @RequestParam Integer language,
                                                   @AuthenticationPrincipal UserDetails userDetails)
            throws IOException, ExecutionException, InterruptedException {
        return recognizeService.recognizeByMediaFile(file, userDetails.getUsername(), language);
    }

    @GetMapping("/priceList")
    public ModelAndView getPriceListPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("priceLIst");
        return modelAndView;
    }
}

