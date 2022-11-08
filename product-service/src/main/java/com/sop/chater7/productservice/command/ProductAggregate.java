package com.sop.chater7.productservice.command;

import com.example.lab11.core.command.ReserveProductCommand;
import com.sop.chater7.productservice.core.event.ProductReserveEvent;
import com.sop.chater7.productservice.core.event.PrtoductCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Aggregate
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;
    private String title;
    private BigDecimal price;
    private Integer quantity;

    public ProductAggregate() {

    }

    @CommandHandler
    public ProductAggregate(CreateProductCommand createProductCommand) {
        if(createProductCommand.getPrice().compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("Price cannot be less than or equal to zero");
        }

        if(createProductCommand.getTitle() == null || createProductCommand.getTitle().isBlank()){
            throw new IllegalArgumentException("Title cannot be empty");
        }

        PrtoductCreatedEvent prtoductCreatedEvent = new PrtoductCreatedEvent();
        BeanUtils.copyProperties(createProductCommand, prtoductCreatedEvent);
        AggregateLifecycle.apply(prtoductCreatedEvent);
    }

    @CommandHandler
    public void handler(ReserveProductCommand reserveProductCommand) {
        if(quantity < reserveProductCommand.getQuantity()) {
            throw new IllegalArgumentException("Insufficient umber of times in stock");
        }
        ProductReserveEvent productReserveEvent = ProductReserveEvent.builder()
                .orderId(reserveProductCommand.getOrderId())
                .productId(reserveProductCommand.getProductId())
                .quantity(reserveProductCommand.getQuantity())
                .userId(reserveProductCommand.getUserId())
                .build();

        AggregateLifecycle.apply(productReserveEvent);
    }
    @EventSourcingHandler
    public void on(PrtoductCreatedEvent prtoductCreatedEvent){
        this.productId = prtoductCreatedEvent.getProductId();
        this.title = prtoductCreatedEvent.getTitle();
        this.price = prtoductCreatedEvent.getPrice();
        this.quantity = prtoductCreatedEvent.getQuantity();
    }

    @EventSourcingHandler
    public void on(ProductReserveEvent productReserveEvent){
        this.quantity -= productReserveEvent.getQuantity();
    }
}
