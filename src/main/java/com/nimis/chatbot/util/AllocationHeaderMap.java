package com.nimis.chatbot.util;

import java.util.Map;

public class AllocationHeaderMap {

    public static final Map<String, String> MAP = Map.ofEntries(
            Map.entry("loannumber", "loanNumber"),
            Map.entry("customername", "customerName"),
            Map.entry("product", "product"),
            Map.entry("segment", "segment"),
            Map.entry("posamt", "posAmount"),
            Map.entry("emi", "emi"),
            Map.entry("branch", "branch"),
            Map.entry("buckettag", "bucket")
    );
}
