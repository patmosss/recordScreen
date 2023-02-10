package recordSCR.design;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import recordSCR.design.ModuleImpl.FunctionPackageMenu;
import recordSCR.design.ModuleImpl.Recording;
import recordSCR.pojo.BaseInfo;

import java.lang.reflect.InvocationTargetException;

/**
 * @author: tree
 * @description:
 * @date: 2023/2/10
 */
public class Builder {

    public IMenu base(BaseInfo baseInfo) throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return new FunctionPackageMenu().appendRecording(new Recording(baseInfo));
    }
}
