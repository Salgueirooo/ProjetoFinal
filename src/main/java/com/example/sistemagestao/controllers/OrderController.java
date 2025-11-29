package com.example.sistemagestao.controllers;

import com.example.sistemagestao.domain.User;
import com.example.sistemagestao.dto.*;
import com.example.sistemagestao.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PutMapping("/make")
    public void makeOrder(@RequestBody OrderRequestDTO data, @AuthenticationPrincipal User user) {
        orderService.makeOrder(data, user);
    }

    @PutMapping("/cancel/{id}")
    public void cancelOrder(@PathVariable Long id, @AuthenticationPrincipal User user) {
        orderService.cancelOrder(id, user);
    }

    @PutMapping("/set-acceptance-status/{id}")
    public void setAcceptanceStatus(@PathVariable Long id, @RequestBody OrderUpdateAcceptanceDTO data) {
        orderService.setAcceptanceStatus(id, data);
    }

    @PutMapping("/set-order-ready/{id}")
    public void setOrderReadyStatus(@PathVariable Long id) {
        orderService.setOrderReady(id);
    }

    @PutMapping("/set-order-delivered/{id}")
    public void setOrderDelivered(@PathVariable Long id) {
        orderService.setOrderDelivered(id);
    }

    @PostMapping("/add-product")
    public void addProduct(@RequestBody OrderDetailsRequestDTO data,  @AuthenticationPrincipal User user) {
        orderService.addProduct(data, user);
    }

    @DeleteMapping("/remove-product")
    public void removeProduct(@RequestBody OrderDetailsRequestDTO data, @AuthenticationPrincipal User user) {
        orderService.removeProduct(data, user);
    }

    @PutMapping("/upgrade-product")
    public void upgradeProduct(@RequestBody OrderDetailsUpgradeRequestDTO data, @AuthenticationPrincipal User user) {
        orderService.upgradeQuantity(data, user);
    }

    @GetMapping("/order-in-cart/{bakery_id}")
    public OrderInCartResponseDTO getOrderInCart(@PathVariable Long bakery_id, @AuthenticationPrincipal User user) {
        return orderService.getOrderInCart(bakery_id, user);
    }

    @GetMapping("/all-by-user/{bakery_id}")
    public List<OrderWReviewResponseDTO> getAllByUser(@PathVariable Long bakery_id, @AuthenticationPrincipal User user) {
        return orderService.getAllOrdersByUser(bakery_id, user);
    }

    @GetMapping("/search-day-by-user/{bakery_id}")
    public List<OrderWReviewResponseDTO> searchOrdersByUser(@PathVariable Long bakery_id, @RequestParam LocalDate date, @AuthenticationPrincipal User user) {
        return orderService.searchOrdersByUser(bakery_id, user, date);
    }

    @GetMapping("/search-email-day/{bakery_id}")
    public List<OrderResponseDTO> getAllByEmailAndDay(@PathVariable Long bakery_id, @RequestParam String email, @RequestParam LocalDate date) {
        return orderService.getAllOrdersByDayAndEmail(bakery_id, email, date);
    }

    @GetMapping("/get-accepted-by-date/{bakery_id}")
    public List<OrderResponseDTO> getAcceptedByDate(@PathVariable Long bakery_id, @RequestParam LocalDate date,  @RequestParam(required = false) String username) {
        if(username == null){
            return orderService.getAllAcceptedOrdersByDay(bakery_id, date);
        }
        else {
            return orderService.getAllAcceptedOrdersByDayAndUsername(bakery_id, username, date);
        }
    }

    @GetMapping("/get-ready-by-date/{bakery_id}")
    public List<OrderResponseDTO> getReadyByDate(@PathVariable Long bakery_id, @RequestParam LocalDate date,  @RequestParam(required = false) String username) {
        if(username == null){
            return orderService.getAllReadyOrdersByDay(bakery_id, date);
        }
        else {
            return orderService.getAllReadyOrdersByDayAndUsername(bakery_id, username, date);
        }
    }

    @GetMapping("/get-all-pending/{bakery_id}")
    public List<OrderResponseDTO> getAllPending(@PathVariable Long bakery_id) {
        return orderService.getAllPendingOrders(bakery_id);
    }
}
