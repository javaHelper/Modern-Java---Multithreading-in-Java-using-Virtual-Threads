package org.example.completablefuture;

import org.example.domain.Product;
import org.example.domain.ProductInfo;
import org.example.domain.Reviews;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;
import org.example.util.LoggerUtil;

import java.util.concurrent.CompletableFuture;

public class ProductServiceUsingCompletableFuture {
    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;

    public ProductServiceUsingCompletableFuture(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) {

        //Calls are asynchronous
        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Reviews> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        //Functional and Call back based
        return CompletableFuture.allOf(cfProductInfo, cfReview)
                .thenApply(v -> {
                    return new Product(productId, cfProductInfo.join(), cfReview.join());
                })
                .join();
    }

    public CompletableFuture<Product> retrieveProductDetails_CF(String productId) {

        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Reviews> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        return cfProductInfo
                .thenCombine(cfReview, (productInfo, review) -> new Product(productId, productInfo, review));
    }

    public Product retrieveProductDetails_exceptionhandling(String productId) {
        CompletableFuture<ProductInfo> cfProductInfo = CompletableFuture.supplyAsync(() -> productInfoService.retrieveProductInfo(productId));
        CompletableFuture<Reviews> cfReview = CompletableFuture.supplyAsync(() -> reviewService.retrieveReviews(productId));

        return CompletableFuture.allOf(cfProductInfo, cfReview)
                .exceptionally(throwable -> {
                    LoggerUtil.log("Exception Occurred in the business logic " + throwable.getMessage());
                    throw new RuntimeException(throwable);
                })
                .thenApply(v -> new Product(productId, cfProductInfo.join(), cfReview.join()))
                .join();
    }


    static void main(String[] args) {
        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();

        ProductServiceUsingCompletableFuture productService = new ProductServiceUsingCompletableFuture(productInfoService, reviewService);

        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        LoggerUtil.log("Product is " + product);
    }
}
