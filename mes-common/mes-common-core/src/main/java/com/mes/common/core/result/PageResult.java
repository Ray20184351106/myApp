package com.mes.common.core.result;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * 分页返回结果
 */
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据列表 */
    private List<T> list;

    /** 总数 */
    private long total;

    /** 当前页 */
    private long pageNum;

    /** 每页数量 */
    private long pageSize;

    /** 总页数 */
    private long pages;

    public static <T> PageResult<T> build(List<T> list, long total, long pageNum, long pageSize) {
        PageResult<T> result = new PageResult<>();
        result.setList(list);
        result.setTotal(total);
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        result.setPages((total + pageSize - 1) / pageSize);
        return result;
    }
}
