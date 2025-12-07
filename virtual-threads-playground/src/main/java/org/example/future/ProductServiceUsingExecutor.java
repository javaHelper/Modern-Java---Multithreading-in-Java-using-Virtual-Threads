package org.example.future;


import org.example.domain.Product;
import org.example.domain.ProductInfo;
import org.example.domain.Reviews;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;
import org.example.util.LoggerUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProductServiceUsingExecutor {

    static ExecutorService executorService = Executors.newFixedThreadPool(6);

    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;

    public ProductServiceUsingExecutor(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
    }

    public Product retrieveProductDetails(String productId) throws ExecutionException, InterruptedException, TimeoutException {

        Future<ProductInfo> productInfoFuture = executorService.submit(() -> productInfoService.retrieveProductInfo(productId));
        Future<Reviews> reviewFuture = executorService.submit(() -> reviewService.retrieveReviews(productId));

        //ProductInfo productInfo = productInfoFuture.get(); // This is a  blocking call.
        ProductInfo productInfo = productInfoFuture.get(2, TimeUnit.SECONDS);
        Reviews reviews = reviewFuture.get(); // This is a  blocking call.
        //Review review = reviewFuture.get(2, TimeUnit.SECONDS);

        return new Product(productId, productInfo, reviews);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        ProductInfoService productInfoService = new ProductInfoService();
        ReviewService reviewService = new ReviewService();
        ProductServiceUsingExecutor productService = new ProductServiceUsingExecutor(productInfoService, reviewService);
        String productId = "ABC123";
        Product product = productService.retrieveProductDetails(productId);
        LoggerUtil.log("Product is " + product);
        executorService.shutdown();
    }

}
