package com.example.demo.services;

import com.example.demo.model.VideoDetails;
import com.example.demo.util.Constants;
import com.github.kokorin.jaffree.StreamType;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.demo.util.Constants.*;
import static com.example.demo.util.GlobalConstants.FILE_TYPE_M4A;

@Service
public class MediaService {
    public String downloadAudioByURL(String videoUrl) {
        String fileName = UUID.randomUUID() + FILE_TYPE_M4A;
        String downloadPath = DOWNLOAD_BASE_PATH + fileName;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    YOUTUBE_DOWNLOADER_PATH,
                    "-f", "bestaudio",
                    "-o", downloadPath,
                    videoUrl
            );
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (reader.readLine() != null) {
            }
            if (process.waitFor() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder errorOutput = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorOutput.append(line).append(System.lineSeparator());
                }
                if (errorOutput.toString().contains("format is not available")) {
                    return downloadVideoByURLAndConvert(videoUrl);
                }
                throw new RuntimeException(errorOutput.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed youtube video download process", e);
        }
        return fileName;
    }

    public String downloadVideoByURLAndConvert(String videoUrl) throws InterruptedException, IOException {
        String fileName = UUID.randomUUID().toString();
        ProcessBuilder processBuilder = new ProcessBuilder(
                YOUTUBE_DOWNLOADER_PATH,
                "-f", "worst",
                "-o", DOWNLOAD_BASE_PATH + fileName,
                videoUrl
        );
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (reader.readLine() != null) {
        }
        if (process.waitFor() != 0) {
            throw new RuntimeException("youtube-dl failed");
        }
        return convertUsersFile(fileName);
    }

    public VideoDetails getYoutubeVideoDetails(String videoUrl) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                    YOUTUBE_DOWNLOADER_PATH,
                    "--get-duration",
                    "--get-title",
                    videoUrl
            );
            processBuilder.environment().put("PYTHONIOENCODING", "UTF-8");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String name = reader.readLine();
            String duration = reader.readLine();
            if (process.waitFor() != 0) {
                throw new RuntimeException("Failed to get video duration");
            }
            return new VideoDetails(name, extractSecondsFromString(duration));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve video duration", e);
        }
    }

    public String downloadUsersFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString();
        String downloadPath = DOWNLOAD_BASE_PATH + fileName;
        file.transferTo(Path.of(downloadPath));
        return fileName;
    }

    public String convertUsersFile(String sourceFileName) {
        String fileName = UUID.randomUUID() + ".mp3";

        FFmpeg.atPath(new File(CONVERTER_PATH).toPath())
                .addArguments("-loglevel", "error") // Подавляем вывод логов
                .addInput(UrlInput.fromPath(Path.of(DOWNLOAD_BASE_PATH + sourceFileName)))
                .addOutput(UrlOutput.toPath(Path.of(DOWNLOAD_BASE_PATH + fileName))
                        .setFormat("mp3")
                        .setCodec(StreamType.AUDIO, "mp3")
                ).execute();

        removeMediaFile(sourceFileName);
        return fileName;
    }

    public int getMediaFileDuration(String fileName) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(MEDIA_FILE_ANALYZER_PATH, "-v", "error", "-show_entries",
                "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", DOWNLOAD_BASE_PATH + fileName);
        Process process = processBuilder.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            return Math.round(Float.parseFloat(reader.readLine()));
        }
    }

    public String getFacebookVideoPreview(String url) {
        try {
            Process process = new ProcessBuilder(Constants.YOUTUBE_DOWNLOADER_PATH, "-j", url)
                    .redirectErrorStream(true)
                    .start();
            StringBuilder json = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) json.append(line);
            }
            process.waitFor();

            String embed;
            Matcher m = Pattern.compile("\"webpage_url\"\\s*:\\s*\"(.*?)\"")
                    .matcher(json.toString());
            if (m.find()) {
                embed = m.group(1);
            } else {
                m = Pattern.compile("\"id\"\\s*:\\s*\"(.*?)\"")
                        .matcher(json.toString());
                if (m.find()) {
                    embed = "https://www.facebook.com/facebook/videos/" + m.group(1) + "/";
                } else {
                    return null;
                }
            }
            return "https://www.facebook.com/plugins/video.php?href=" +
                    URLEncoder.encode(embed, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void removeMediaFile(String fileName) {
        File file = new File(DOWNLOAD_BASE_PATH + fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    private int extractSecondsFromString(String duration) {
        String[] parts = duration.split(":");
        return switch (parts.length) {
            case 1 -> Integer.parseInt(parts[0]);
            case 2 -> Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            case 3 -> Integer.parseInt(parts[0]) * 3600 + Integer.parseInt(parts[1]) * 60 + Integer.parseInt(parts[2]);
            default -> throw new RuntimeException("Invalid duration");
        };
    }
}