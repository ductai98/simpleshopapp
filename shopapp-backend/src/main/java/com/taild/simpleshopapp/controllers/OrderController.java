package com.taild.simpleshopapp.controllers;


import com.taild.simpleshopapp.component.SecurityUtils;
import com.taild.simpleshopapp.dtos.Response;
import com.taild.simpleshopapp.dtos.orders.OrderDTO;
import com.taild.simpleshopapp.dtos.orders.OrderResponse;
import com.taild.simpleshopapp.enums.OrderStatus;
import com.taild.simpleshopapp.models.Order;
import com.taild.simpleshopapp.models.User;
import com.taild.simpleshopapp.services.orders.IOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;
    private final SecurityUtils securityUtils;

    @PostMapping("")
    public ResponseEntity<Response> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) throws Exception {
        if(result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(
                    Response.builder()
                            .message(String.join(";", errorMessages))
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
        }
        User loginUser = securityUtils.getLoggedInUser();
        if(orderDTO.getUserId() == null) {
            orderDTO.setUserId(loginUser.getId());
        }
        Order orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(Response.builder()
                        .message("Insert order successfully")
                        .data(orderResponse)
                        .status(HttpStatus.OK)
                        .build());
    }


    @GetMapping("/user/{user_id}")
    public ResponseEntity<Response> getOrders(@Valid @PathVariable("user_id") Long userId) {
        User loginUser = securityUtils.getLoggedInUser();
        boolean isUserIdBlank = userId == null || userId <= 0;
        List<OrderResponse> orderResponses = orderService.findByUserId(isUserIdBlank ? loginUser.getId() : userId);
        return ResponseEntity.ok(Response
                        .builder()
                        .message("Get list of orders successfully")
                        .data(orderResponses)
                        .status(HttpStatus.OK)
                        .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getOrder(@Valid @PathVariable("id") Long orderId) {
        Order existingOrder = orderService.getOrderById(orderId);
        OrderResponse orderResponse = OrderResponse.fromOrder(existingOrder);
        return ResponseEntity.ok(new Response(
                "Get order successfully",
                    HttpStatus.OK,
                    orderResponse
                ));
    }


    @PutMapping("/{id}")
    public ResponseEntity<Response> updateOrder(
            @Valid @PathVariable long id,
            @Valid @RequestBody OrderDTO orderDTO) throws Exception {

        Order order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(new Response("Update order successfully", HttpStatus.OK, order));
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<Response> cancelOrder(
            @Valid @PathVariable long id) throws Exception {
        Order order = orderService.getOrderById(id);
        // Kiểm tra xem người dùng hiện tại có phải là người đã đặt đơn hàng hay không
        User loginUser = securityUtils.getLoggedInUser();
        if (loginUser.getId() != order.getUser().getId()) {
            return ResponseEntity.badRequest().body(Response.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .data(null)
                    .message("You do not have permission to cancel this order")
                    .build());
        }

        OrderDTO orderDTO = OrderDTO.builder()
                .userId(order.getUser().getId())
                .status(OrderStatus.CANCELLED)
                .build();;

        order = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(
                new Response(
                        "Cancel order successfully",
                        HttpStatus.OK,
                        order)
        );
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteOrder(@Valid @PathVariable Long id) {
        //xóa mềm => cập nhật trường active = false
        orderService.deleteOrder(id);
        String message = "Xóa đơn hàng thành công";
        return ResponseEntity.ok(
                Response.builder()
                        .message(message)
                        .build()
        );
    }


    @GetMapping("/get-orders-by-keyword")
    public ResponseEntity<Response> getOrdersByKeyword(
            @RequestParam(defaultValue = "", required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<OrderResponse> orderPage = orderService
                                        .getOrdersByKeyword(keyword, pageRequest)
                                        .map(OrderResponse::fromOrder);
        // Lấy tổng số trang
        int totalPages = orderPage.getTotalPages();
        List<OrderResponse> orderResponses = orderPage.getContent();
        return ResponseEntity.ok().body(Response.builder()
                .message("Get orders successfully")
                .status(HttpStatus.OK)
                .data(orderResponses)
                .build());
    }


    @PutMapping("/{id}/status")
    public ResponseEntity<Response> updateOrderStatus(
            @Valid @PathVariable Long id,
            @RequestParam String status) throws Exception {
        // Gọi service để cập nhật trạng thái
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        // Trả về phản hồi thành công
        return ResponseEntity.ok(Response.builder()
                .message("Order status updated successfully")
                .status(HttpStatus.OK)
                .data(OrderResponse.fromOrder(updatedOrder))
                .build());
    }
}
