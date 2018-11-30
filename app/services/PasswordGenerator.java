package services;
import java.util.Random;

/**
 *
 * @author tool-taro.com
 */
public class PasswordGenerator {

    public static String main() {

        //パスワード桁数
        int length = 16;
        //記号使用有無
        boolean useSign = false;
        //アルファベット大文字小文字のスタイル(normal/lowerCase/upperCase)
        String style = "normal";

        //生成処理
        StringBuilder result = new StringBuilder();
        //パスワードに使用する文字を格納
        StringBuilder source = new StringBuilder();
        //数字
        for (int i = 0x30; i < 0x3A; i++) {
            source.append((char) i);
        }
        //記号
        if (useSign) {
            for (int i = 0x21; i < 0x30; i++) {
                source.append((char) i);
            }
        }
        //アルファベット小文字
        switch (style) {
            case "lowerCase":
                break;
            default:
                for (int i = 0x41; i < 0x5b; i++) {
                    source.append((char) i);
                }
                break;
        }
        //アルファベット大文字
        switch (style) {
            case "upperCase":
                break;
            default:
                for (int i = 0x61; i < 0x7b; i++) {
                    source.append((char) i);
                }
                break;
        }

        int sourceLength = source.length();
        Random random = new Random();
        while (result.length() < length) {
            result.append(source.charAt(Math.abs(random.nextInt()) % sourceLength));
        }
		return String.valueOf(result);
    }
}