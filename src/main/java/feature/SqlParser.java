package feature;

import command.CommandHandler;
import utils.CamelCaseUtil;
import utils.printer.Printer;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlParser implements CommandHandler<String> {
    public static void main(String[] args){
        String sql = "CREATE TABLE `wechat_bill` (\n" +
                "  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',\n" +
                "  `bill_date` varchar(12) NOT NULL COMMENT '记账时间',\n" +
                "  `wechat_payment_no` varchar(30) NOT NULL COMMENT '微信支付业务单号',\n" +
                "  `biz_type` varchar(20) NOT NULL COMMENT '微信支付业务单号',\n" +
                "  `seller_id` varchar(45) NOT NULL DEFAULT '' COMMENT '店铺id',\n" +
                "  `smid` varchar(32) NOT NULL DEFAULT '' COMMENT '电商二级商户号',\n" +
                "  `apply_account` varchar(50) DEFAULT '' COMMENT '资金变更提交申请人',\n" +
                "  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '收支金额(元)',\n" +
                "  `created_time` datetime NOT NULL COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `idx_bill_date` (`bill_date`),\n" +
                "  KEY `idx_seller_id` (`seller_id`)\n" +
                ") ENGINE=InnoDB AUTO_INCREMENT=236 DEFAULT CHARSET=utf8mb4 COMMENT='微信账单';";
        SqlParser sqlParser = new SqlParser();
        sqlParser.createDTO(sql);
        sqlParser.createJavaMapper(sql);
        sqlParser.createXmlMapper(sql);
    }

    public static Map<String, TableInfo> sqlTableInfoMap = new HashMap<>();


    public void createDTO(String sql){
        TableInfo tableInfo = getTableInfo(sql);
        Printer.printOut("@Data");
        Printer.printOut("public class %s{", tableInfo.beanName);
        tableInfo.beanFields.entrySet().forEach(entry -> {
            Printer.printOut("  private %s %s;", entry.getValue(), entry.getKey());
        });
        Printer.printOut("}");
    }

    public void createJavaMapper(String sql){
        TableInfo tableInfo = getTableInfo(sql);
        Printer.printOut("public interface %sMapper {", tableInfo.beanName);
        Printer.printOut("\n  int insert(%s record);", tableInfo.beanName);
        Printer.printOut("\n  void batchInsert(@Param(\"list\") List<%s> list);", tableInfo.beanName);
        Printer.printOut("}");
    }

    public void createXmlMapper(String sql){
        TableInfo tableInfo = getTableInfo(sql);
        // 头信息
        Printer.printOut("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\">");
        Printer.printOut("<mapper namespace=\"%sMapper\">", tableInfo.beanName);
        // select字段映射BaseColumn
        Printer.printOut("  <sql id=\"BaseColumn\">");
        printFieldsSql(tableInfo.fields.keySet(), "    %s as %s", s->s, CamelCaseUtil::toCamelCase);
        Printer.printOut("  </sql>\n");
        // insert
        Printer.printOut("  <insert id=\"insert\">");
        Printer.printOut("      INSERT INTO `%s` (", tableInfo.tableName);
        printFieldsSql(tableInfo.fields.keySet(), "     `%s`", s->s);
        Printer.printOut("      )VALUES(");
        printFieldsSql(tableInfo.beanFields.keySet(), "     #{%s}", s->s);
        Printer.printOut("      )");
        Printer.printOut("  </insert>\n");
        // batchInsert
        Printer.printOut("  <insert id=\"batchInsert\">");
        Printer.printOut("      INSERT INTO `%s` (", tableInfo.tableName);
        printFieldsSql(tableInfo.fields.keySet(), "     `%s`", s->s);
        Printer.printOut("      )VALUES");
        Printer.printOut("      <foreach collection=\"list\" item=\"item\" separator=\",\">");
        Printer.printOut("          (");
        printFieldsSql(tableInfo.beanFields.keySet(), "             #{item.%s}", s->s);
        Printer.printOut("          )");
        Printer.printOut("      </foreach>");
        Printer.printOut("  </insert>\n");

        Printer.printOut("</mapper>");
    }

    private static void printFieldsSql(Set<String> fields, String format, Function<String, String>... argFuns){
        int count = 0;
        for(String key : fields){
            List<String> args = Arrays.stream(argFuns)
                    .map(fun -> fun.apply(key))
                    .collect(Collectors.toList());
            if(++count != fields.size()){
                Printer.printOut(format+",", args.toArray(new String[0]));
            } else {
                Printer.printOut(format, args.toArray(new String[0]));
            }
        };
    }

    public static TableInfo getTableInfo(String sql){
        if(sqlTableInfoMap.containsKey(sql)){
            return sqlTableInfoMap.get(sql);
        }
        TableInfo tableInfo = new TableInfo(sql);
        sqlTableInfoMap.put(sql, tableInfo);
        return tableInfo;
    }
}

class TableInfo{
    private static final String LINE_SEPARATOR = "\\n";
    private static final String KEY_SEPARATOR = "`";
    private static final Map<String, String> javaTypeMap = new HashMap(){{
        put("INT", "Integer");
        put("CHAR","String");
        put("VARCHAR","String");
        put("DECIMAL","BigDecimal");
        put("BIT","Boolean");
        put("BOOLEAN","Boolean");
        put("TINYINT","Byte");
        put("INTEGER","Integer");
        put("BIGINT","Long");
        put("FLOAT","Double");
        put("DOUBLE","Double");
        put("JSON","String");
        put("DATETIME","Date");
    }};
    String tableName;
    Map<String, String> fields = new LinkedHashMap<>();

    String beanName;
    Map<String, String> beanFields = new LinkedHashMap<>();

    TableInfo(String sql){
        String[] lines = sql.split(LINE_SEPARATOR);
        tableName = getKeyWord(lines[0]);
        beanName = CamelCaseUtil.toCamelCase(tableName, true);
        // 解析字段, 第一行和最后一行不解析
        for(int i = 1; i < lines.length-1; i++){
            String line = lines[i];
            if(!line.contains("COMMENT")){
                break; // 默认所有字段都有 COMMENT,而索引定义语句没有
            }
            String fieldName = getKeyWord(line);
            String type = line.trim().split(" ")[1].trim();
            if(type.contains("(")){
                type = type.substring(0, type.indexOf("("));
            }
            fields.put(fieldName, type);
            beanFields.put(CamelCaseUtil.toCamelCase(fieldName), javaTypeMap.get(type.toUpperCase()));
        }
    }

    private static String getKeyWord(String line){
        int first = line.indexOf(KEY_SEPARATOR);
        int last = line.lastIndexOf(KEY_SEPARATOR);
        return line.substring(first+1, last);
    }
}
