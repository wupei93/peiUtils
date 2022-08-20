package utils.printer;

import utils.PropertiesUtil;

import java.io.PrintWriter;

public class Printer {
    private static PrintWriter logPrinter = PrinterEnum.valueOf(PropertiesUtil.get("logPrinter")).getPrintWriter();
    private static PrintWriter outPrinter = PrinterEnum.valueOf(PropertiesUtil.get("outPrinter")).getPrintWriter();

    static{
        Runtime.getRuntime().addShutdownHook(new Thread(Printer::onShutdown));
    }

    public static void printLog(String message, String... args){
        logPrinter.printf(message, args);
        logPrinter.println();
    }

    public static void printOut(String message, String... args){
        outPrinter.printf(message, args);
        outPrinter.println();
    }

    public static void onShutdown(){
        logPrinter.flush();
        outPrinter.flush();
    }
}
