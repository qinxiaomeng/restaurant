package com.ej.restaurant.result;

import com.ej.restaurant.params.PageParam;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page<E> implements Serializable {

    private static final long serialVersionUID = 2759730160214944840L;

    private int currentPage = 1; //当前页数
    private long totalPage;       //总页数
    private long totalNumber;    //总记录数
    private List<E> list;        //数据集

    public Page(PageParam pageParam, long totalNumber, List<E> list){
        super();
        this.currentPage = pageParam.getCurrentPage();
        this.totalNumber = totalNumber;
        this.list = list;
        this.totalPage = totalNumber % pageParam.getPageSize() == 0 ? totalNumber / pageParam.getPageSize() : totalNumber / pageParam.getPageSize() + 1;
    }
}
