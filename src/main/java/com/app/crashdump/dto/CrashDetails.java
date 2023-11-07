package com.app.crashdump.dto;

import java.io.Serializable;

public record CrashDetails(String userId, String timestamp, String errorMessage) implements Serializable {
}
