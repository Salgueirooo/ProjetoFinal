package com.example.sistemagestao.dto;

import java.util.List;

public record STUserDTO(
        List<STMonthlyOrdersResponseDTO> monthlyOrders,
        List<STProductSalesDTO> top10Products,
        STClientSpendingDTO totalSpent
) {}
