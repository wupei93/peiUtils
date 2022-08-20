package utils;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.OptionalLong;
import com.xiaohongshu.sns.id_mapping.IdMapping;
import com.xiaohongshu.sns.id_mapping.IdType;

public class IdUtils {

    public static Long userId2Long(String userId) {
        try {
            return IdMapping.oidToLong(userId, IdType.USER);
        } catch (FileNotFoundException e) {
            System.out.printf("mapping userId failed: %s , %s", userId, e);
        }
        return null;
    }

    public static String long2UserId(long userId) {
        try {
            return IdMapping.longToOid(userId);
        } catch (FileNotFoundException e) {
            System.out.printf("mapping userId failed: %s , %s", userId, e);
        }
        return null;
    }
}
