package com.oauth.jwt.common.data.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@UtilityClass
public class ListUtils {

    /**
     * @param total    总数
     * @param pageNo   页码
     * @param pageSize 页容量
     * @param records  截取后的集合
     * @return
     */
    public Map<String, Object> page(int total, int pageNo, int pageSize, List records) {
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("records", records);
        result.put("size", pageSize);
        result.put("pageTotal", Math.ceil((double) total / pageSize));
        result.put("current", pageNo);
        return result;
    }

}
