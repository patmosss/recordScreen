package recordSCR.module;

import recordSCR.pojo.BaseInfo;
import recordSCR.pojo.WatermarkInfo;

/**
 * 功能模块
 */
public interface  Module {
    // 录制模块
    void recordScreen(BaseInfo baseInfo, WatermarkInfo watermarkInfo) throws Exception;
    // 水印模块
    WatermarkInfo watermark() ;
}
