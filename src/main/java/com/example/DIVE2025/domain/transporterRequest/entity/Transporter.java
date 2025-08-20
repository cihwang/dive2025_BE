package com.example.DIVE2025.domain.transporterRequest.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transporter {

    private Long id;
    private String username;
    private String password;
    private String role;
    private String storeName;
    private String tel;
    private String addr;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

}
