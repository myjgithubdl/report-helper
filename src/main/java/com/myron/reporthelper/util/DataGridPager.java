package com.myron.reporthelper.util;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * easyui gridview控件分页类 <br>
 *
 * @author Tom Deng
 * @date 2017-03-25
 */
@Data
@NoArgsConstructor
public class DataGridPager {
    private Integer page = 1;
    private Integer rows = 50;
    private String sort;
    private String order;


}
