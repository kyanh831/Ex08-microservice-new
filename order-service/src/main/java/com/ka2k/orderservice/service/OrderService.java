package com.ka2k.orderservice.service;

import com.ka2k.orderservice.dto.InventoryResponse;
import com.ka2k.orderservice.dto.OrderLineItemsDto;
import com.ka2k.orderservice.dto.OrderRequest;
import com.ka2k.orderservice.model.Order;
import com.ka2k.orderservice.model.OrderLineItems;
import com.ka2k.orderservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final WebClient webClient;
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> items = orderRequest.getOrderLineItemsDtoList().stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItems(items);

        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        InventoryResponse[] result = webClient.get()
                .uri("http://localhost:3004/api/inventory",uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductInStock = Arrays.stream(result).allMatch(InventoryResponse::isInStock);


        if(allProductInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not stock, please try again");
        }

        orderRepository.save(order);
    }

    private OrderLineItems mapToDto(OrderLineItemsDto o) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(o.getPrice());
        orderLineItems.setQuantity(o.getQuantity());
        orderLineItems.setSkuCode(o.getSkuCode());
        return orderLineItems;
    }
}
