package com.devtools.ai.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String plan;
    private Set<String> roles;
}
