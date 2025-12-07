package org.example.service;


import org.example.domain.ProductInfo;
import org.example.domain.ProductOption;
import org.example.util.CommonUtil;
import org.example.util.LoggerUtil;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

import static org.example.util.CommonUtil.objectMapper;
import static org.example.util.CommonUtil.requestBuilder;


public class ProductInfoService {

    //virtual-threads/src/main/resources/deliveryDetails.json
    public  static String PRODUCT_INFO_URL = "http://127.0.0.1:8000/virtual-threads/src/main/resources/productInfo.json";


    public ProductInfo retrieveProductInfoHttp(String productId) throws IOException, InterruptedException {
        //httpclient
        var httpClient  = CommonUtil.httpClient;
        var httpRequest = requestBuilder(PRODUCT_INFO_URL);

        var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        LoggerUtil.log("response code " + response.statusCode());

        //parser to parse JSON to Java Record INstance
      return objectMapper.readValue(response.body(), ProductInfo.class);

    }

    public ProductInfo retrieveProductInfo(String productId) {
        CommonUtil.sleep(1000);
//        throw new RuntimeException("retrieveProductInfo");
        List<ProductOption> productOptions = List.of(new ProductOption("64GB", "Black", 699.99),
                new ProductOption("128GB", "Black", 749.99));
        LoggerUtil.log("retrieveProductInfo after Delay");
        return new ProductInfo(productId, productOptions);
    }



    public ProductInfo retrieveProductInfoV2(String productId) {
        CommonUtil.sleep(2000);
        List<ProductOption> productOptions = List.of(new ProductOption("64GB", "Black", 699.99),
                new ProductOption("128GB", "Black", 749.99));
        LoggerUtil.log("retrieveProductInfo after Delay v2 ");
        return new ProductInfo(productId, productOptions);
    }

    public ProductInfo retrieveProductInfoV3(String productId) {
        CommonUtil.sleep(8000);
        List<ProductOption> productOptions = List.of(new ProductOption("64GB", "Black", 699.99),
                new ProductOption("128GB", "Black", 749.99));
        LoggerUtil.log("retrieveProductInfo after Delay v3 ");
        return new ProductInfo(productId, productOptions);
    }

    public ProductInfo retrieveProductInfo_MultipleSources(String productId){

        try(var scope = new StructuredTaskScope.ShutdownOnSuccess<ProductInfo>()){

            scope.fork(()-> retrieveProductInfo(productId));
            scope.fork(()-> retrieveProductInfoV2(productId));
            scope.fork(()-> retrieveProductInfoV3(productId));
            return scope.join().result();
        }catch (Exception e){
            throw  new RuntimeException(e);
        }

    }

}
