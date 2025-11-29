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
import java.util.Comparator;
import java.util.List;

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
    @Autowired
    private ProductReviewService productReviewService;

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

        if (!order.getOrderState().equals(OrderStates.PENDING) && !order.getOrderState().equals(OrderStates.ACCEPTED))
            throw new IllegalStateException("Não foi possível cancelar esta Encomenda.");

        if (order.getOrderState().equals(OrderStates.ACCEPTED)){
            long hoursBetween = ChronoUnit.HOURS.between(LocalDateTime.now(), order.getDate());
            if (hoursBetween < 24) {
                throw new IllegalStateException("Só é possível cancelar esta Encomenda com 24h de antecedência.");
            }
        }

        order.setOrderState(OrderStates.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    public void setAcceptanceStatus(Long id, OrderUpdateAcceptanceDTO data) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Encomenda não encontrada."));

        if (!order.getOrderState().equals(OrderStates.PENDING))
            throw new IllegalStateException("Não é possível definir o estado de aceitação desta Encomenda.");

        if (data.acceptance()) {
            order.setOrderState(OrderStates.ACCEPTED);
        } else {
            order.setOrderState(OrderStates.REJECTED);
        }

        order.setStaffNotes(data.staffNotes());

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

        Order order = orderRepository.findByUserIdAndBakery_IdAndOrderState(user.getId(), data.bakeryId(), OrderStates.INCART);

        if (order == null)
            throw new EntityNotFoundException("Carrinho não encontrado.");

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
        Order order = orderRepository.findByUserIdAndBakery_IdAndOrderState(user.getId(), data.bakeryId(), OrderStates.INCART);

        if (order == null)
            throw new EntityNotFoundException("Carrinho não encontrado.");

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

    @Transactional
    public void upgradeQuantity(OrderDetailsUpgradeRequestDTO data, User user) {
        OrderDetails orderDetails = orderDetailsRepository.findById(data.orderDetailsId())
                .orElseThrow(() -> new EntityNotFoundException("Detalhes da Encomenda não encontrados."));

        if(!user.equals(orderDetails.getOrder().getUser()))
            throw new AuthorizationDeniedException("Acesso negado.");

        if (!orderDetails.getOrder().getOrderState().equals(OrderStates.INCART))
            throw new IllegalStateException("Não é possível alterar a quantidade de um Produto desta Encomenda.");

        if (data.quantity() < 1) {
            throw new IllegalArgumentException("A quantidade do Produto deve ser maior que zero.");
        }

        orderDetails.setQuantity(data.quantity());
        orderDetailsRepository.save(orderDetails);
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

    private OrderWReviewResponseDTO buildOrderWReviewDTO(Order order) {

        List<OrderDetailsWReviewResponseDTO> details = order.getOrderDetails().stream()
                .sorted(Comparator.comparing(od -> od.getProduct().getName().toLowerCase()))
                .map(od -> {

                    boolean wasReviewed = productReviewService.wasReviewed(
                            od.getId()
                    );

                    return new OrderDetailsWReviewResponseDTO(
                            od,
                            wasReviewed
                    );
                })
                .toList();

        return new OrderWReviewResponseDTO(
                order.getId(),
                order.getUser().getName(),
                order.getUser().getPhone_number(),
                order.getDate(),
                order.getRequestDate(),
                order.getOrderState().getState(),
                order.getClientNotes(),
                order.getStaffNotes(),
                details
        );
    }

    public List<OrderWReviewResponseDTO> getAllOrdersByUser(Long bakeryId, User user) {
        if(!userRepository.existsById(user.getId()))
            throw new EntityNotFoundException("Utilizador não encontrado.");

        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime date = LocalDate.now().atStartOfDay();

        return orderRepository.findAllByUserIdAndBakery_IdAndOrderStateNotInAndDateGreaterThanEqualOrderByDateAsc(user.getId(), bakeryId, List.of(OrderStates.INCART, OrderStates.CANCELLED), date)
                .stream()
                .map(this::buildOrderWReviewDTO)
                .toList();
    }

    public List<OrderWReviewResponseDTO> searchOrdersByUser(Long bakeryId, User user, LocalDate date) {
        if(!userRepository.existsById(user.getId()))
            throw new EntityNotFoundException("Utilizador não encontrado.");

        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByUserIdAndBakery_IdAndOrderStateNotAndDateBetweenOrderByDateAsc(user.getId(), bakeryId, OrderStates.INCART, startDate, endDate)
                .stream()
                .map(this::buildOrderWReviewDTO)
                .toList();
    }

    public List<OrderResponseDTO> getAllOrdersByDayAndEmail(Long bakeryId, String email, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndUser_EmailAndOrderStateNotAndDateBetweenOrderByDateAsc(
                        bakeryId, email, OrderStates.INCART, startOfDay, endOfDay
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

    public List<OrderResponseDTO> getAllReadyOrdersByDay(Long bakeryId, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndOrderStateAndDateBetweenOrderByDateAsc(
                        bakeryId, OrderStates.READY, startOfDay, endOfDay
                )
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public List<OrderResponseDTO> getAllReadyOrdersByDayAndUsername(Long bakeryId, String username, LocalDate date) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return orderRepository.findAllByBakery_IdAndUserNameContainsIgnoreCaseAndOrderStateAndDateBetweenOrderByDateAsc(
                        bakeryId, username, OrderStates.READY, startOfDay, endOfDay
                )
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }

    public List<OrderResponseDTO> getAllPendingOrders(Long bakeryId) {
        if(!bakeryRepository.existsById(bakeryId))
            throw new EntityNotFoundException("Pastelaria não encontrada.");

        return orderRepository.findAllByBakery_IdAndOrderStateOrderByDateAsc(bakeryId, OrderStates.PENDING)
                .stream()
                .map(OrderResponseDTO::new)
                .toList();
    }


}
