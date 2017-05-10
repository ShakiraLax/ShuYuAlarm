package com.sypm.shuyualarm.api;


import com.sypm.shuyualarm.data.DataResult;
import com.sypm.shuyualarm.data.Order;
import com.sypm.shuyualarm.data.OrderBySn;
import com.sypm.shuyualarm.data.StoreName;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ShuYuService {

    /*账号登陆*/
    @FormUrlEncoded
    @POST("site/login.html")
    Call<DataResult> login(@Field("staffSn") String staffSn, @Field("password") String password, @Field("registrationId") String registrationId);

    /*获取指派未接受订单*/
    @GET("shiporder/getorder.html")
    Call<OrderBySn> getOrder();

    /*获取门店名称*/
    @FormUrlEncoded
    @POST("doorbell/v1/store/get")
    Call<StoreName> getStoreNameByStoreSn(@Field("storeSn") String storeSn);

    /*根据门店编号获取订单*/
    @FormUrlEncoded
    @POST("doorbell/v1/order/get")
    Call<Order> getOrderByStoreSn(@Field("orderSn") String orderSn);

}
