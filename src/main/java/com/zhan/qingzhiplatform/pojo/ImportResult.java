package com.zhan.qingzhiplatform.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 导入结果类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    private int success;
    private int fail;
    private List<String> errors = new ArrayList<>();

    public void addSuccess() { success++; }
    public void addFail(String error) { fail++; errors.add(error); }
}
