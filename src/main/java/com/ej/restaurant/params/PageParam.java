package com.ej.restaurant.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageParam {
    private int beginLine;
    private int pageSize = 10;
    private int currentPage = 1;

    public int getBeginLine(){
        return pageSize * (currentPage - 1);
    }
}
