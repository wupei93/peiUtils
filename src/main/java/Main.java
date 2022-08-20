import feature.Describe;
import utils.IdUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class Main {

    public static void main(String[] args) throws Exception {
        String method = args[0];
        Method declaredMethod = Main.class.getDeclaredMethod(method, args.getClass());
        declaredMethod.invoke(null, (Object) args);
    }

    @Describe("help: 帮助")
    public static void help(String[] args) {
        Arrays.stream(Main.class.getDeclaredMethods())
                .map(method -> method.getDeclaredAnnotation(Describe.class))
                .filter(Objects::nonNull)
                .forEach( describe -> System.out.println(describe.value()));
    }

    @Describe("long2UserId: 用户id转换 long->string")
    public static void long2UserId(String[] args) {
        String longUserId = args[1];
        Long userId = Long.parseLong(longUserId);
        System.out.println(IdUtils.long2UserId(userId));
    }

    @Describe("userId2Long: 用户id转换 string->long")
    public static void userId2Long(String[] args) {
        String userId = args[1];
        System.out.println(IdUtils.userId2Long(userId));
    }
}
