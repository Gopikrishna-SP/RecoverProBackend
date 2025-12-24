package com.nimis.chatbot.util;

import java.util.Map;

public class ExcelHeaderMap {

    public static final Map<String, String> HEADER_MAP = Map.ofEntries(
            Map.entry("SEGMENT", "segment"),
            Map.entry("PRODUCT", "product"),
            Map.entry("ZONE", "zone"),
            Map.entry("STATE", "state"),
            Map.entry("BRANCH", "branch"),
            Map.entry("LOCATION", "location"),
            Map.entry("LOANNUMBER", "loanNumber"),
            Map.entry("CUSTOMER NAME", "customerName"),
            Map.entry("POS", "pos"),
            Map.entry("EMI", "emi"),
            Map.entry("BKT TAG", "bktTag")
    );
}
