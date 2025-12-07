package org.example.domain;

public record ProductV2(String productId,
                        ProductInfo productInfo,
                        Reviews reviews,
                        DeliveryDetails deliveryDetails) {
}
