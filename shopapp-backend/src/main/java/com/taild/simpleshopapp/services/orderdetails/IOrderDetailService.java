package com.taild.simpleshopapp.services.orderdetails;

import com.taild.simpleshopapp.dtos.orderdetails.OrderDetailDTO;
import com.taild.simpleshopapp.exceptions.DataNotFoundException;
import com.taild.simpleshopapp.models.OrderDetail;

import java.util.List;

public interface IOrderDetailService {
    OrderDetail createOrderDetail(OrderDetailDTO newOrderDetail) throws Exception;
    OrderDetail getOrderDetail(Long id) throws DataNotFoundException;
    OrderDetail updateOrderDetail(Long id, OrderDetailDTO newOrderDetailData)
            throws DataNotFoundException;
    void deleteById(Long id);
    List<OrderDetail> findByOrderId(Long orderId);


}
