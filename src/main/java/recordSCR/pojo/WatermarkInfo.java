package recordSCR.pojo;

import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/1
 */
public class WatermarkInfo {

    public Point point;
    public Scalar scalar;

    public WatermarkInfo() {
    }

    public WatermarkInfo(Point point, Scalar scalar) {
        this.point = point;
        this.scalar = scalar;
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

    @Override
    public String toString() {
        return "WatermarkInfo{" +
                "point=" + point +
                ", scalar=" + scalar +
                '}';
    }
}
