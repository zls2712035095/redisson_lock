package com.zls.main.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserRegDto implements Serializable {
    private String userName;

    private String password;

}
