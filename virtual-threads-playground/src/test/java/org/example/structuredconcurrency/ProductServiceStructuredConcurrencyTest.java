package org.example.structuredconcurrency;

import org.example.service.DeliveryService;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceStructuredConcurrencyTest {
    @Spy
    ProductInfoService productInfoService;

    @Spy
    ReviewService reviewService;

    @Spy
    DeliveryService deliveryService;

    @InjectMocks
    ProductServiceStructuredConcurrency productServiceStructuredConcurrency;

    @Test
    void retrieveProductDetails() {
        var product = productServiceStructuredConcurrency.retrieveProductDetails("ABC");
        assertNotNull(product);
        assertNotNull(product.productInfo());
        assertNotNull(product.reviews());
    }

    @Test
    void retrieveProductDetailsV2() {
        var productV2 = productServiceStructuredConcurrency.retrieveProductDetailsV2("ABC");
        assertNotNull(productV2);
        assertNotNull(productV2.productInfo());
        assertNotNull(productV2.reviews());
        assertNotNull(productV2.deliveryDetails());
    }

    @Test
    void retrieveProductDetails_Exception() {
        when(reviewService.retrieveReviews(anyString()))
                .thenThrow(new RuntimeException("Exception calling review Service"));

        var exception = Assertions.assertThrows(RuntimeException.class,
                ()-> productServiceStructuredConcurrency.retrieveProductDetails("ABC"));

        assertTrue(exception.getMessage().contains("Exception calling review Service"));
    }
}
