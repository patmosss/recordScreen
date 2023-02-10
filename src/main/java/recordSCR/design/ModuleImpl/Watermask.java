package recordSCR.design.ModuleImpl;

import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import recordSCR.design.Module;
import recordSCR.pojo.WatermarkInfo;
import java.io.File;

/**
 *  水印模块
 */
public class Watermask implements Module {


    private WatermarkInfo watermarkInfo = new WatermarkInfo();
    private Mat logo;
    private Mat mask;
    private Mat mat;

    double alpha = 0.5;  // 图像透明权重值,0-1之间

    public void jmarkInfo(WatermarkInfo watermarkInfo) {

        if (watermarkInfo != null) {
            String imagePath = watermarkInfo.getImagePath();
            if (imagePath != null) {
                File f = new File(imagePath);
                if (f.exists() && f.isFile()) {
                    logo = opencv_imgcodecs.imread(imagePath);
                    mask = opencv_imgcodecs.imread(imagePath, 0);
                    opencv_imgproc.threshold(mask, mask,254,255, opencv_imgcodecs.IMWRITE_PNG_BILEVEL);
                } else {
                    System.out.println("水印图片路径出错,请重试!");
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void scene()  {
        if (watermarkInfo != null) {
            // 加文字水印，opencv_imgproc.putText（图片，水印文字（无法识别中文），文字位置，字体，字体大小，字体颜色，字体粗度，文字反锯齿，是否翻转文字）
            opencv_imgproc.putText(getMat(),
                    watermarkInfo.getText() == null ? "" : watermarkInfo.getText(),
                    watermarkInfo.getPoint(),
                    opencv_imgproc.CV_FONT_VECTOR0,
                    watermarkInfo.getFontSize(),
                    watermarkInfo.getScalar(),
                    watermarkInfo.getFontThickness(),
                    watermarkInfo.getText_antialiasing(),
                    watermarkInfo.getIsFlip());

            if (logo != null) {
                // 定义感兴趣区域(位置，logo图像大小)
                Mat ROI = getMat().apply(new Rect(400, 350, logo.cols(), logo.rows()));
                opencv_core.addWeighted(ROI, alpha, logo, 1.0 - alpha, 0.0, ROI);
                // 把logo图像复制到感兴趣区域

                if (watermarkInfo.getIsCover() == 1) {
                    logo.copyTo(ROI, mask);
                }
            }
        }
    }

    @Override
    public String desc() {
        return "水印";
    }

    public Mat getMat() {
        return mat;
    }

    public void setMat(Mat mat) {
        this.mat = mat;
    }

    public WatermarkInfo getWatermarkInfo() {
        return watermarkInfo;
    }

    public Watermask() {
    }
}
