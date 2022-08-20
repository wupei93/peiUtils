package feature;

import command.CommandHandler;
import utils.IdUtils;
import utils.printer.Printer;

public class IdTransfer implements CommandHandler<String> {

    @Describe("long2UserId: 用户id转换 long->string")
    public void long2UserId(String args) {
        String longUserId = args;
        Long userId = Long.parseLong(longUserId);
        Printer.printOut(IdUtils.long2UserId(userId));
    }

    @Describe("userId2Long: 用户id转换 string->long")
    public void userId2Long(String args) {
        String userId = args;
        Printer.printOut(IdUtils.userId2Long(userId).toString());
    }
}
