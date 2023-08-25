package com.zls.main.dto;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BookRobDto implements Serializable {
    private Integer userId;
    private String bookNo;
}
