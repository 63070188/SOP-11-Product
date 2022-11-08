package com.sop.chater7.productservice.query;

import com.sop.chater7.productservice.core.ProductEntity;
import com.sop.chater7.productservice.core.data.ProductRepository;
import com.sop.chater7.productservice.core.event.ProductReserveEvent;
import com.sop.chater7.productservice.core.event.PrtoductCreatedEvent;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class ProductEventsHandler {
    private final ProductRepository productRepository;

    public ProductEventsHandler(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventHandler
    public void on(PrtoductCreatedEvent event) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(event, productEntity);
        productRepository.save(productEntity);
    }

    @EventHandler
    public void on(ProductReserveEvent productReserveEvent) {
        ProductEntity productEntity = productRepository.findByProductId(productReserveEvent.getProductId());
        productEntity.setQuantity(productEntity.getQuantity() - productReserveEvent.getQuantity());
        productRepository.save(productEntity);
    }
}

