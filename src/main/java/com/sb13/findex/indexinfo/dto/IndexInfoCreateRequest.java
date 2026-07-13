package com.sb13.findex.indexinfo.dto;

import java.math.*;
import java.time.*;

public record IndexInfoCreateRequest(
        String inedxClassification,
        String indexName,
        int employedItemsCount,
        LocalDate basePointInTime,
        BigDecimal baseIndex,
        boolean favorite
) {
}
