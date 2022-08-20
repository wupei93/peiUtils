package utils;

public class CamelCaseUtil {

    public static String toCamelCase(String underlineName, boolean firstCharUpperCase) {
        String[] words = underlineName.split("_");
        StringBuilder sb = new StringBuilder();
        sb.append(words[0]);
        for(int i = 1; i < words.length; i++) {
            sb.append(firstCharUpperCase(words[i]));
        }
        String result = sb.toString();
        if(firstCharUpperCase){
            result = firstCharUpperCase(result);
        }
        return result;
    }

    public static String toCamelCase(String underlineName) {
        return toCamelCase(underlineName, false);
    }

    public static String firstCharUpperCase(String word){
        if(word == null || word.length() == 0){
            return word;
        }
        char rowFirstChar = word.charAt(0);
        char firstChar = Character.isUpperCase(rowFirstChar) ? rowFirstChar : (char)(rowFirstChar-32);
        return firstChar + word.substring(1);
    }
}
