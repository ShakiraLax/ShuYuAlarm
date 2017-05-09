package com.sypm.shuyualarm.data;

import java.util.List;


public class DataResult<T> {

    /**
     * status : 1
     * message : 登陆成功
     * data{ }
     */

    public String status;
    public String number;
    public String type;
    public String msg;
    public String storeName;
    public List<T> list;
    public String[] array;

}
