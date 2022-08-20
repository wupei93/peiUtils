package utils.printer;

import java.io.PrintWriter;

public enum PrinterEnum {
    /**
     * 控制台输出
     */
    SYSTEM_OUT(new PrintWriter(System.out));
    PrintWriter printWriter;

    PrinterEnum(java.io.PrintWriter printWriter){
        this.printWriter = printWriter;
    }

    public PrintWriter getPrintWriter(){
        return printWriter;
    }
}
