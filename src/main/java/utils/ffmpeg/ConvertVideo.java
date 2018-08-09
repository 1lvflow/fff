package utils.ffmpeg;

public class ConvertVideo {
    public static void convertVedio(String inputPath) throws FFmpegException {
        String ffmpegPath =getFfmpegPath();
        String outputPath =getOutputPath(inputPath);
        FfmpegUtil.ffmpeg(ffmpegPath, inputPath,outputPath);

    }

    private static String getFfmpegPath(){
        return "";
    }

    private static String getOutputPath(String inputPath) {
        return inputPath.substring(0,inputPath.lastIndexOf(".")).toLowerCase() + ".mp4";
    }

}
