package org.example.domain;

import java.util.List;

public record ProductInfo(String productId,
                          List<ProductOption> productOptions) {
}
