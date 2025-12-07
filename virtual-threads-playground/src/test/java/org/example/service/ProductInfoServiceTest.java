package org.example.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductInfoServiceTest {

    @Spy
    ProductInfoService productInfoService = new ProductInfoService();

    @Test
    void retrieveProductInfo_MultipleSources() {
        var productInfo = productInfoService.retrieveProductInfo_MultipleSources("ABC");
        assertNotNull(productInfo);
    }

    @Test
    void retrieveProductInfo_simulateError() {
        when(productInfoService.retrieveProductInfo(anyString())).thenThrow(new RuntimeException("Exception Occurred"));
        when(productInfoService.retrieveProductInfoV2(anyString())).thenThrow(new RuntimeException("Exception Occurred"));

        var productInfo = productInfoService.retrieveProductInfo_MultipleSources("ABC");
        assertNotNull(productInfo);
    }

    /*@Test
    @Disabled
    void retrieveProductInfo_http() throws IOException, InterruptedException {
        var productInfo = productInfoService.retrieveProductInfoHttp("ABC");
        LoggerUtil.log("productInfo : "+ productInfo);
        assertNotNull(productInfo);

    }*/
}