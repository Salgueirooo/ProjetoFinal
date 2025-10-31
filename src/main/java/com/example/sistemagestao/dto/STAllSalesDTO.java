package com.example.sistemagestao.dto;

import java.util.List;

public record STAllSalesDTO (
        List<STProductSalesDTO> productSalesList,
        STProductSalesDTO topProductSale,
        List<STClientSalesDTO> clientSalesList,
        STClientSalesDTO topClientSale
) {
    public STAllSalesDTO (
            List<STProductSalesDTO> productSalesList,
            STProductSalesDTO topProductSale,
            List<STClientSalesDTO> clientSalesList,
            STClientSalesDTO topClientSale
    ) {
        this.productSalesList = productSalesList;
        this.topProductSale = topProductSale;
        this.clientSalesList = clientSalesList;
        this.topClientSale = topClientSale;
    }
}
