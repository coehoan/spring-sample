package org.example.awstest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserModifyDto {
    private Integer id;
    private String email;
    private int age;
    private String password;
    private String newPassword;
    private String type;
}
