package com.example.crossfit.record;

import java.util.List;

public record BulkRecordResponse(
        int createdCount,
        List<String> failedMembers,
        String message
) {
    public static BulkRecordResponse of(int createdCount, List<String> failedMembers) {
        String message;
        if (failedMembers.isEmpty()) {
            message = "기록이 등록되었습니다.";
        } else {
            message = "일부 회원 기록 등록에 실패했습니다.";
        }
        return new BulkRecordResponse(createdCount, failedMembers, message);
    }
}
