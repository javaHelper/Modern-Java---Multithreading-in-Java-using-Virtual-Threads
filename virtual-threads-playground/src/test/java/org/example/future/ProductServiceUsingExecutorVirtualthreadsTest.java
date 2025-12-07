package org.example.future;

import org.example.domain.Product;
import org.example.domain.ProductInfo;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceUsingExecutorVirtualthreadsTest {
    @Spy
    ProductInfoService productInfoService;

    @Spy
    ReviewService reviewService;

    @InjectMocks
    ProductServiceUsingExecutorVirtualThreads productServiceVT;

    @Test
    @DisplayName("Retrieve product details with virtual threads")
    void retrieveProductDetails() throws ExecutionException, InterruptedException, TimeoutException {
        Product product = productServiceVT.retrieveProductDetails("Macbook Pro");
        System.out.println(product.toString());

        assertThat(product)
                .isNotNull()
                .extracting(Product::productId)
                .isEqualTo("Macbook Pro");

        assertThat(product.productInfo())
                .isNotNull()
                .extracting(ProductInfo::productId)
                .isEqualTo("Macbook Pro");
    }

    @Test
    @DisplayName("")
    void retrieveProductDetailsException() throws InterruptedException {
        when(productInfoService.retrieveProductInfo(anyString()))
                .thenThrow(new RuntimeException("Exception Occurred"));
        var exception = Assertions.assertThrows(ExecutionException.class, () -> productServiceVT.retrieveProductDetails("ABC"));
        assertEquals("java.lang.RuntimeException: Exception Occurred", exception.getMessage());

        Thread.sleep(2000);
    }
}
