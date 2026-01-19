package com.example.sistemagestao.dto;

import java.util.List;

public record STAllBakeryDTO(
        List<STMonthlyOrdersResponseDTO> monthlyOrders,
        List<STProductSalesDTO> productSalesList,
        List<STProductCostDTO> productCostList,
        STProductSalesDTO topProductSale,
        STProductCostDTO topProductCost,
        STClientSalesDTO topClientSale,
        STClientSpendingDTO topClientSpending
) {
}
