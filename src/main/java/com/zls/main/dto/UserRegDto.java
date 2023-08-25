package com.weikun.main.dto;

import lombok.*;

import java.io.Serializable;

/**
 * 创建人：Jason
 * 创建时间：2021/10/26
 * 描述你的类：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class UserRegDto implements Serializable {
    private String userName;

    private String password;

}
