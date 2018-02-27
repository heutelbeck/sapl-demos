package io.sapl.demo.pil;

import java.util.Map;

class SAPLRequest {
    static final String DEP_AP = "depAp";
    static final String ARR_AP = "arrAp";
    static final String FLT_NO = "fltNo";
    static final String DATE = "date";
    static final String AC_REG = "acReg";
    static final String CLASSIFICATION = "classification";
    static final String STD_AC = "DABTF";
    static final String STD_STATUS = "ACTIVE";
    static final String PERS_ID = "personalIdentifier";
    static final String OPS_STATUS = "operationalStatus";
    static final String RECURRENT = "recurrent";
    static final String PIL_RETRIEVE = "PIL:RETRIEVE";

    private static final String REQUEST_TEMPLATE = "{\"subject\":{%s},\"action\":\"%s\",\"resource\":{%s},\"environment\":{%s}}";
    private static final String QUOTE_DELIM = "\"";
    private static final String COMMA = ",";

    private Map<String, Object> subject;
    private String action;
    private Map<String, Object> resource;
    private Map<String, Object> environment;

    SAPLRequest(Map<String, Object> s, String a, Map<String, Object> r, Map<String, Object> e) {
        subject = s;
        action = a;
        resource = r;
        environment = e;
    }

    String toJsonNode() {
        return String.format(REQUEST_TEMPLATE, formatMap(subject), action, formatMap(resource), formatMap(environment));
    }

    private String formatMap(Map<String, Object> map) {
        if (map.size() == 0) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                result.append(QUOTE_DELIM).append(entry.getKey()).append(QUOTE_DELIM).append(":");
                if (entry.getValue() instanceof String) {
                    result.append(QUOTE_DELIM).append(entry.getValue()).append(QUOTE_DELIM).append(COMMA);
                } else {
                    result.append(entry.getValue()).append(COMMA);
                }
            }
            result.setLength(result.length() - 1);

            return result.toString();
        }
    }
}
