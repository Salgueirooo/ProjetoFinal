package com.example.sistemagestao.dto;

import java.util.List;

public record STAllCostDTO(
        List<STProductCostDTO> productCostDTO,
        STProductCostDTO highestCostProduct,
        List<STClientSpendingDTO> clientSpendingDTO,
        STClientSpendingDTO highestClientSpending
) {
    public STAllCostDTO(
            List<STProductCostDTO> productCostDTO,
            STProductCostDTO highestCostProduct,
            List<STClientSpendingDTO> clientSpendingDTO,
            STClientSpendingDTO highestClientSpending
    ){
        this.productCostDTO = productCostDTO;
        this.highestCostProduct = highestCostProduct;
        this.clientSpendingDTO = clientSpendingDTO;
        this.highestClientSpending = highestClientSpending;
    }
}
