package com.bytogether.chatservice.dto.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ViewablePeriod {
    private LocalDateTime from;
    private LocalDateTime until;

    public boolean isInPeriod(LocalDateTime time) {
        boolean afterStart = !time.isBefore(from);
        boolean beforeEnd = until == null || !time.isAfter(until);
        return afterStart && beforeEnd;
    }
}