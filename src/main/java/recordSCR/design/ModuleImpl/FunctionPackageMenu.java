package recordSCR.design.ModuleImpl;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import recordSCR.design.IMenu;
import recordSCR.design.Module;

import java.lang.reflect.InvocationTargetException;


public class FunctionPackageMenu implements IMenu {

    @Override
    public IMenu appendRecording(Module module) throws FFmpegFrameGrabber.Exception, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        module.scene();
        return this;
    }
}
