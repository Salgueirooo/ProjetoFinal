package com.example.sistemagestao.services;

import com.example.sistemagestao.domain.*;
import com.example.sistemagestao.dto.*;
import com.example.sistemagestao.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private BakeryRepository bakeryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderDetailsRepository orderDetailsRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void initialize(Long bakeryId, User user) {
        Bakery bakery = bakeryRepository.findById(bakeryId)
                .orElseThrow(() -> new EntityNotFoundException("Pastelaria não encontrada."));

        Order order = new Order(user, bakery);

        orderRepository.save(order);
    }

    @Transactional
    public void updateUnitaryPrices(Long orderId) {

        if (!orderRepository.existsById(orderId))
            throw new EntityNotFoundException("Encomenda não encontrada.");

        List<OrderDetails> orderDetailsList = orderDetailsRepository.findAllByOrderId(orderId);

        if (orderDetailsList.isEmpty()) {
            throw new EntityNotFoundException("A encomenda não possui produtos associados.");
        }

        for (OrderDetails orderDetails : orderDetailsList) {
            Product product = orderDetails.getProduct();

            if (product == null) {
                throw new IllegalStateException("Produto não encontrado no detalhe da encomenda.");
            }

            orderDetails.setPrice(product.getPrice());
            orderDetails.setDiscount(product.getDiscount());
        }

        orderDetailsRepository.saveAll(orderDetailsList);
    }

    @Transactional
    public void makeOrder(OrderRequestDTO data, User user) {
        Order order = orderRepository.findById(data.id())
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if(!user.equals(order.getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if(!order.getOrderState().equals(OrderStates.INCART))
            throw new IllegalStateException("A encomenda já foi feita.");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime orderDate = data.date();

        if (orderDate == null)
            throw new IllegalArgumentException("A data da encomenda é obrigatória.");


        if(orderDetailsRepository.countAllByOrderId(order.getId()) < 1)
            throw new IllegalArgumentException("A encomenda não tem produtos.");

        long hoursBetween = ChronoUnit.HOURS.between(now, orderDate);
        if (hoursBetween < 24) {
            throw new IllegalStateException("A data da encomenda deve ter pelo menos 24 horas de antecedência.");
        }

        updateUnitaryPrices(order.getId());

        order.setOrderState(OrderStates.PENDING);
        order.setClientNotes(data.clientNotes());
        order.setRequestDate(now);
        order.setDate(data.date());
        orderRepository.save(order);

        initialize(order.getBakery().getId(), user);
    }

    @Transactional
    public void cancelOrder(Long id, User user) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if (!user.equals(order.getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if (order.getOrderState().equals(OrderStates.CANCELLED))
            throw new IllegalStateException("A Encomenda já foi cancelada.");

        if (!order.getOrderState().equals(OrderStates.PENDING))
            throw new IllegalStateException("Não foi possível cancelar esta Encomenda.");

        order.setOrderState(OrderStates.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void setAcceptanceStatus(Long id, boolean acceptanceStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if (!order.getOrderState().equals(OrderStates.PENDING))
            throw new IllegalStateException("Não é possível definir o estado de aceitação desta Encomenda.");

        if (acceptanceStatus) {
            order.setOrderState(OrderStates.ACCEPTED);
        } else {
            order.setOrderState(OrderStates.REJECTED);
        }

        orderRepository.save(order);
    }

    @Transactional
    public void setOrderReady(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if (!order.getOrderState().equals(OrderStates.ACCEPTED))
            throw new IllegalStateException("Não é possível definir esta Encomenda como pronta.");

        order.setOrderState(OrderStates.READY);
        orderRepository.save(order);
    }

    @Transactional
    public void setOrderDelivered(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if (!order.getOrderState().equals(OrderStates.READY))
            throw new IllegalStateException("Não é possível definir esta Encomenda como entregue.");

        order.setOrderState(OrderStates.DELIVERED);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        List<OrderDetails> details = order.getOrderDetails();
        orderDetailsRepository.deleteAll(details);

        orderRepository.delete(order);
    }

    @Transactional
    public void addProduct(OrderDetailsRequestDTO data, User user) {
        Order order = orderRepository.findById(data.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if(!user.equals(order.getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if (!order.getOrderState().equals(OrderStates.INCART))
            throw new IllegalStateException("Não é possível adicionar um Produto a esta Encomenda.");

        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        OrderDetails orderDetails = orderDetailsRepository.findByOrderIdAndProductId(order.getId(), product.getId());

        if (orderDetails == null) {
            orderDetails = new OrderDetails(order, product, 1);
        } else {
            orderDetails.setQuantity(orderDetails.getQuantity() + 1);
        }

        orderDetailsRepository.save(orderDetails);
    }

    @Transactional
    public void removeProduct(OrderDetailsRequestDTO data, User user) {
        Order order = orderRepository.findById(data.orderId())
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if(!user.equals(order.getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if (!order.getOrderState().equals(OrderStates.INCART))
            throw new IllegalStateException("Não é possível remover um Produto desta Encomenda.");

        Product product = productRepository.findById(data.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado."));

        OrderDetails orderDetails = orderDetailsRepository.findByOrderIdAndProductId(order.getId(), product.getId());

        if (orderDetails == null)
            throw new EntityNotFoundException("Detalhes da encomenda não encontrados.");

        if(orderDetails.getQuantity() == 1) {
            orderDetailsRepository.delete(orderDetails);
        } else {
            orderDetails.setQuantity(orderDetails.getQuantity() - 1);
            orderDetailsRepository.save(orderDetails);
        }
    }

    public OrderInCartResponseDTO getOrderInCart(Long bakeryId, User user) {
        if(!userRepository.existsById(user.getId()))
            throw new EntityNotFoundException("Utilizador não encontrado.");

        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return new OrderInCartResponseDTO(orderRepository.findByUserIdAndBakery_IdAndOrderState(
                user.getId(), bakeryId, OrderStates.INCART
        ));
    }

    public List<OrderResponseDTO> getAllOrdersByUser(Long bakeryId, User user) {
        if(!userRepository.existsById(user.getId()))
            throw new EntityNotFoundException("Utilizador não encontrado.");

        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return orderRepository.findAllByUserIdAndBakery_IdAndOrderStateNotOrderByDateDesc(user.getId(), bakeryId, OrderStates.INCART)
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public List<OrderResponseDTO> getAllOrdersByDayAndUsername(Long bakeryId, String username, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateNotAndDateBetweenOrderByDateAsc(
                        bakeryId, username, OrderStates.INCART, startOfDay, endOfDay
                )
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    //usar request params
    public List<OrderResponseDTO> getAllAcceptedOrdersByDay(Long bakeryId, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndOrderStateAndDateBetweenOrderByDateAsc(
                bakeryId, OrderStates.ACCEPTED, startOfDay, endOfDay
            )
            .stream()
            .map(OrderResponseDTO::new)
            .toList();
    }

    public List<OrderResponseDTO> getAllAcceptedOrdersByDayAndUsername(Long bakeryId, String username, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateAndDateBetweenOrderByDateAsc(
                        bakeryId, username, OrderStates.ACCEPTED, startOfDay, endOfDay
                )
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public List<OrderResponseDTO> getAllPendingOrders(Long bakeryId) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return orderRepository.findAllByBakery_IdAndOrderStateOrderByRequestDateAsc(bakeryId, OrderStates.PENDING)
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }


}
