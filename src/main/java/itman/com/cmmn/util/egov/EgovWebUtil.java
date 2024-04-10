package itman.com.cmmn.util.egov;

import java.util.regex.Pattern;

public class EgovWebUtil {
    public static String clearXSSMinimum(String value) {
        if (value == null || value.trim().equals("")) {
            return "";
        }

        String returnValue = value;

        returnValue = returnValue.replaceAll("&", "&amp;");
        returnValue = returnValue.replaceAll("<", "&lt;");
        returnValue = returnValue.replaceAll(">", "&gt;");
        returnValue = returnValue.replaceAll("\"", "&#34;");
        returnValue = returnValue.replaceAll("\'", "&#39;");
        returnValue = returnValue.replaceAll("\\.", "&#46;");
        returnValue = returnValue.replaceAll("%2E", "&#46;");
        returnValue = returnValue.replaceAll("%2F", "&#47;");
        return returnValue;
    }

    public static String clearXSSMaximum(String value) {
        String returnValue = value;
        returnValue = clearXSSMinimum(returnValue);

        returnValue = returnValue.replaceAll("%00", null);

        returnValue = returnValue.replaceAll("%", "&#37;");

        // \\. => .

        returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
        returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\
        returnValue = returnValue.replaceAll("\\./", ""); // ./
        returnValue = returnValue.replaceAll("%2F", "");

        return returnValue;
    }

    public static String clearXSS(String value) {
        if (value == null || value.trim().equals("")) {
            return "";
        }

        String returnValue = value;
        returnValue = returnValue.replaceAll("&", "&amp;");
        returnValue = returnValue.replaceAll("%2E", "&#46;");
        returnValue = returnValue.replaceAll("%2F", "&#47;");
        returnValue = returnValue.replaceAll("<", "&lt;");
        returnValue = returnValue.replaceAll(">", "&gt;");
        returnValue = returnValue.replaceAll("%3C", "&lt;");
        returnValue = returnValue.replaceAll("%3E", "&gt;");

        return returnValue;
    }

    public static String filePathBlackList(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("\\.\\.", "");

        return returnValue;
    }

    /**
     * 행안부 보안취약점 점검 조치 방안.
     *
     * @param value
     * @return
     */
    public static String filePathReplaceAll(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("/", "");
        returnValue = returnValue.replaceAll("\\\\", "");
        returnValue = returnValue.replaceAll("\\.\\.", ""); // ..
        returnValue = returnValue.replaceAll("&", "");

        return returnValue;
    }

    public static String fileInjectPathReplaceAll(String value) {
        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        returnValue = returnValue.replaceAll("/", "");
        returnValue = returnValue.replaceAll("\\..", ""); // ..
        returnValue = returnValue.replaceAll("\\\\", "");// \
        returnValue = returnValue.replaceAll("&", "");

        return returnValue;
    }

    public static String filePathWhiteList(String value) {
        return value;
    }

    public static boolean isIPAddress(String str) {
        Pattern ipPattern = Pattern.compile("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");

        return ipPattern.matcher(str).matches();
    }

    public static String removeCRLF(String parameter) {
        return parameter.replaceAll("\r", "").replaceAll("\n", "");
    }

    public static String removeSQLInjectionRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("%", "").replaceAll(";", "")
                .replaceAll("-", "").replaceAll("\\+", "").replaceAll(",", "");
    }

    public static String removeOSCmdRisk(String parameter) {
        return parameter.replaceAll("\\p{Space}", "").replaceAll("\\*", "").replaceAll("\\|", "").replaceAll(";", "");
    }

    /**
     * LDAP 파라미터에서 특수문자 제거.
     * 파라미터 별로 제거를 해야 함.
     * 일괄 연결된 파라미터들은 따로 처리해야 함.
     * TODO : LDAP Injection Prevent 로직 추가 필요
     * @param value
     * @return
     */
    public static String removeLDAPInjectionRisk(String value) {

        String returnValue = value;
        if (returnValue == null || returnValue.trim().equals("")) {
            return "";
        }

        /*모든 특수문자 제거*/
//		String match = "[^\uAC00-\uD7A30-9a-zA-Z]";//특수문자 = 한글,숫자,영문 제외
//		returnValue = returnValue.replaceAll(match, "");

        /*특수문자 선택적 제거*/
        returnValue = returnValue.replaceAll("*", "");
        returnValue = returnValue.replaceAll("&", "");
        returnValue = returnValue.replaceAll("|", "");
        returnValue = returnValue.replaceAll("//", "");
        //...
        //개별로 필요한 항목들 추가 필요

        return returnValue;
    }

}
