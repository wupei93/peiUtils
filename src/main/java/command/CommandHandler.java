package command;

import utils.printer.Printer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface CommandHandler<T> {
    default void run(String command, T param) {
        try {
            Method method = this.getClass().getDeclaredMethod(command, param.getClass());
            method.invoke(this, param);
        } catch (Exception e) {
            Printer.printLog("执行命令报错, command:%s,error:%s", command, e.toString());
        }
    }
}
