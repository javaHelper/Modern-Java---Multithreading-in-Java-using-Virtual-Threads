package org.example.service;

import org.example.domain.ProductInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.example.util.LoggerUtil.log;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeliveryServiceTest {

    DeliveryService deliveryService = new DeliveryService();

    @Test
    @Disabled
    void retrieveDeliveryInfoHttp() throws IOException, InterruptedException {
        var deliveryDetails = deliveryService.retrieveDeliveryInfoHttp(new ProductInfo("ABC", List.of()));
        log("deliveryDetails : " + deliveryDetails);

        assertNotNull(deliveryDetails);
    }
}