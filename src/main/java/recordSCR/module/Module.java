package recordSCR.module;

/**
 * 功能模块
 */
public interface Module {
    // 录制模块
    void recordScreen(String outputFile, int audio_device_index, int framerate, String screen_device_index) throws Exception;
    // 水印模块
    void watermark();
}
