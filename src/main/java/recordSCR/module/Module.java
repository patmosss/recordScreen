package recordSCR.module;

import recordSCR.pojo.WatermarkInfo;

/**
 * 功能模块
 */
public interface  Module {
    // 录制模块
    void recordScreen(String outputFile, int audio_device_index, int framerate, String screen_device_index, String watermarkText) throws Exception;
    // 水印模块
    WatermarkInfo watermark() ;
}
