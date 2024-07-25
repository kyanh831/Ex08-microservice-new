package com.ka2k.inventoryservice;

import com.ka2k.inventoryservice.model.Inventory;
import com.ka2k.inventoryservice.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory inventory = new Inventory();
            inventory.setSkuCode("red_code");
            inventory.setQuantity(100);
            Inventory inventory1 = new Inventory();
            inventory1.setSkuCode("green_code");
            inventory1.setQuantity(0);
            inventoryRepository.save(inventory);
            inventoryRepository.save(inventory1);
        };
    }
}
