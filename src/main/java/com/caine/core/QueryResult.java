package com.caine.core;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents query result.
 */
@Getter
@Builder
public class QueryResult {

    private String displayIcon;
    private String displayText;
    private String handleUri;
}
