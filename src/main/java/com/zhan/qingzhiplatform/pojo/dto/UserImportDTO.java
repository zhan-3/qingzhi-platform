package com.zhan.qingzhiplatform.pojo.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportDTO {
    @ExcelProperty(index = 0)
    private String name;

    @ExcelProperty(index = 1)
    private String username;

    @ExcelProperty(index = 2)
    private String password;

    @ExcelProperty(index = 3)
    private String phone;

    @ExcelProperty(index = 4)
    private String email;

    @ExcelProperty(index = 5)
    private String department;

    @ExcelProperty(index = 6)
    private String role;
}
