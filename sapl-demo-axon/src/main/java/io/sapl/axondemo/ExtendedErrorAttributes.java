package io.sapl.axondemo;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class ExtendedErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        var errorAttributes = super.getErrorAttributes(webRequest, options);
        var error = getError(webRequest);
        // sanitize string from leading class names, like org.package.SomeClass:

        var msg = error != null ? error.getMessage().replaceAll("^[a-zA-Z\\.]*:\\s*", "") : "";
        errorAttributes.put("message", msg);
        return errorAttributes;
    }
}