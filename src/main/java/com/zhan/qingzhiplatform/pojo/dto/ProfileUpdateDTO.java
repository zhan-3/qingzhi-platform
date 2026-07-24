package com.zhan.qingzhiplatform.pojo.dto;

import lombok.Data;

@Data
public class ProfileUpdateDTO {
    private String name;
    private String phone;
    private String email;
    private String department;
    private String major;
}
