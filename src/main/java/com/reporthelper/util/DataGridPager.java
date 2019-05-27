package com.reporthelper.util;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * easyui gridview控件分页类 <br>
 *
 * @author Myron
 * @date 2019-03-25
 */
@Data
@NoArgsConstructor
public class DataGridPager {
    private Integer page = 1;
    private Integer rows = 50;
    private String sort;
    private String order;


}
