package org.example.domain;

public record Product(String productId,
                      ProductInfo productInfo,
                      Reviews reviews) {
}
