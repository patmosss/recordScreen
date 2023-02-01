package recordSCR.pojo;

import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/1
 */
public class WatermarkInfo {

    private String text;           //水印文字（无法识别中文）
    private Point point;           //水印位置
    private int fontSize;          //字体大小
    private Scalar scalar;         //文字颜色
    private int fontThickness;     //字体粗度
    private int text_antialiasing; //文字反锯齿
    private boolean isFlip;        //是否翻转


    public WatermarkInfo() {
        text = "";
        point = new Point(200, 200);
        fontSize = 4;
        scalar = new Scalar(0, 0, 0, 0);
        fontThickness = 4;
        text_antialiasing = 0;
        isFlip = false;
    }

    public WatermarkInfo(String text, Point point, int fontSize, Scalar scalar, int fontThickness, int text_antialiasing, boolean isFlip) {
        this.text = text;
        this.point = point;
        this.fontSize = fontSize;
        this.scalar = scalar;
        this.fontThickness = fontThickness;
        this.text_antialiasing = text_antialiasing;
        this.isFlip = isFlip;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getFontThickness() {
        return fontThickness;
    }

    public void setFontThickness(int fontThickness) {
        this.fontThickness = fontThickness;
    }

    public int getText_antialiasing() {
        return text_antialiasing;
    }

    public void setText_antialiasing(int text_antialiasing) {
        this.text_antialiasing = text_antialiasing;
    }

    public boolean getIsFlip() {
        return isFlip;
    }

    public void setIsFlip(boolean isFlip) {
        this.isFlip = isFlip;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Scalar getScalar() {
        return scalar;
    }

    public void setScalar(Scalar scalar) {
        this.scalar = scalar;
    }


}
