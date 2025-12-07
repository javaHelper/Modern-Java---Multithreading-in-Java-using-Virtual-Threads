package org.example.completablefuture;

import org.example.domain.Product;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUsingCompletableFutureTest {
    @Spy
    ProductInfoService productInfoService;

    @Spy
    ReviewService reviewService;

    @InjectMocks
    ProductServiceUsingCompletableFuture productServiceUsingCF;

    @Test
    void retrieveProductDetails() {
        String productId = "ABC123";
        Product product = productServiceUsingCF.retrieveProductDetails(productId);

        assertThat(product);
        assertThat(!product.productInfo().productOptions().isEmpty());
        assertThat(product.reviews());
    }

    @Test
    void retrieveProductDetails_Exception_productInfo() {
        String productId = "ABC123";
        when(productInfoService.retrieveProductInfo(anyString())).thenThrow(new RuntimeException("Exception Occurred in ProductInfo"));

        var exception = Assertions.assertThrows(CompletionException.class, () -> productServiceUsingCF.retrieveProductDetails_exceptionhandling(productId));
        assertTrue(exception.getMessage().contains("Exception Occurred in ProductInfo"));

    }

    @Test
    void retrieveProductDetails_Exception_reviews() {
        String productId = "ABC123";
        when(reviewService.retrieveReviews(anyString())).thenThrow(new RuntimeException("Exception Occurred in Reviews"));

        var exception = Assertions.assertThrows(CompletionException.class, () -> productServiceUsingCF.retrieveProductDetails_exceptionhandling(productId));
        assertTrue(exception.getMessage().contains("Exception Occurred in Reviews"));
    }

    @Test
    @Disabled
    void retrieveProductDetails_Exception_reviews_exceptionally() {
        String productId = "ABC123";
        when(reviewService.retrieveReviews(anyString())).thenThrow(new RuntimeException("Exception Occurred in Reviews"));

        var product = productServiceUsingCF.retrieveProductDetails_exceptionhandling(productId);
        assertNotNull(product);
        assertEquals(0.0, product.reviews().noOfReviews());
    }

    @Test
    void retrieveProductDetails_CF() {
        String productId = "ABC123";
        CompletableFuture<Product> cfProduct = productServiceUsingCF.retrieveProductDetails_CF(productId);
        cfProduct
                .thenAccept((product -> {
                    assertNotNull(product);
                    assertFalse(product.productInfo().productOptions().isEmpty());
                    assertNotNull(product.reviews());
                }))
                .join();

    }
}
