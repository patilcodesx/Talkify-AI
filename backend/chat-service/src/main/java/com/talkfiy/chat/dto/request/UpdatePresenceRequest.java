package com.talkfiy.chat.dto.request;

import com.talkfiy.chat.model.UserPresence;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePresenceRequest {
    
    @NotNull(message = "Status is required")
    private UserPresence.UserStatus status;
    
    private String customStatus;
}
