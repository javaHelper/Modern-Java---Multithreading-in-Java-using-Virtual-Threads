package org.example.service;

import org.mockito.Spy;

class ReviewServiceTest {

    @Spy
    ReviewService reviewService = new ReviewService();

   /* @Test
    @Disabled
    void retrieveReviews() throws IOException, InterruptedException {
        var reviews = reviewService.retrieveReviewsHttp("ABC");
        log("reviews : " + reviews);
        assertNotNull(reviews);

    }*/
}