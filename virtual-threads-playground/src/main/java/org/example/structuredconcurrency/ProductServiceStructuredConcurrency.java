package org.example.structuredconcurrency;

import org.example.domain.Product;
import org.example.domain.ProductV2;
import org.example.service.DeliveryService;
import org.example.service.ProductInfoService;
import org.example.service.ReviewService;

import java.util.concurrent.StructuredTaskScope;


public class ProductServiceStructuredConcurrency {

    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;
    private final DeliveryService deliveryService;

    public ProductServiceStructuredConcurrency(ProductInfoService productInfoService, ReviewService reviewService, DeliveryService deliveryService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
        this.deliveryService = deliveryService;
    }

    public ProductServiceStructuredConcurrency(ProductInfoService productInfoService, ReviewService reviewService) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
        this.deliveryService = null;
    }


    public Product retrieveProductDetails(String productId) {

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){
            //Virtual Threads are created for the below tasks
            // Fork the tasks
            var productInfoSubtask = scope.fork(()->productInfoService.retrieveProductInfo(productId));
            var reviewSubtask = scope.fork(()->reviewService.retrieveReviews(productId));

            //Join the tasks
            scope.join().throwIfFailed(); // This is a completely non blocking call.

            var productInfo = productInfoSubtask.get();
            var reviews = reviewSubtask.get();

            return new Product(productId, productInfo, reviews);
        }catch (Exception e ){
            throw  new RuntimeException(e);
        }

    }

    public ProductV2 retrieveProductDetailsV2(String productId) {

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){
            //Virtual Threads are created for the below tasks
            // Fork the tasks
            var productInfoSubtask = scope.fork(()->productInfoService.retrieveProductInfo(productId));
            var reviewSubtask = scope.fork(()->reviewService.retrieveReviews(productId));

            //Join the tasks
            scope.join().throwIfFailed(); // This is a completely non blocking call.

            var productInfo = productInfoSubtask.get();
            var reviews = reviewSubtask.get();

            var deliveryDetailsTask = scope.fork(() ->deliveryService.retrieveDeliveryInfo(productInfo));
            scope.join().throwIfFailed();

            return new ProductV2(productId, productInfo, reviews, deliveryDetailsTask.get());
        }catch (Exception e ){
            throw  new RuntimeException(e);
        }

    }

    public ProductV2 retrieveProductDetailsHttp(String productId) {

        try(var scope = new StructuredTaskScope.ShutdownOnFailure()){
            //Virtual Threads are created for the below tasks
            // Fork the tasks
            var productInfoSubtask = scope.fork(()->productInfoService.retrieveProductInfoHttp(productId));
            var reviewSubtask = scope.fork(()->reviewService.retrieveReviewsHttp(productId));

            //Join the tasks
            scope.join().throwIfFailed(); // This is a completely non blocking call.

            var productInfo = productInfoSubtask.get();
            var reviews = reviewSubtask.get();

            var deliveryDetailsTask = scope.fork(() ->deliveryService.retrieveDeliveryInfoHttp(productInfo));
            scope.join().throwIfFailed();

            return new ProductV2(productId, productInfo, reviews, deliveryDetailsTask.get());
        }catch (Exception e ){
            throw  new RuntimeException(e);
        }

    }

}
