package com.example.securitylab.util;

import org.apache.commons.text.StringEscapeUtils;

public final class HtmlSanitizer {

    private HtmlSanitizer() {
    }

    public static String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(value.trim());
    }
}
