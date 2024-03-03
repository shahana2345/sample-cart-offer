package com.springboot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartOfferApplicationTests {

    @Test
    public void checkFlatXForOneSegment() throws Exception {
        List<String> segments = new ArrayList<>();
        segments.add("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, true); // able to add offer
    }

    @Test
    public void checkFlatXForMultipleSegments() throws Exception {
        // Test case to check adding an offer for multiple segments
        List<String> segments = Arrays.asList("p1", "p2", "p3");
        OfferRequest offerRequest = new OfferRequest(2, "FLATX", 15, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, true);
    }

    @Test
    public void checkInvalidOfferType() throws Exception {
        // Test case to check handling of an invalid offer type
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(3, "INVALID_OFFER", 5, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, false); // Expecting failure for an invalid offer type
    }

    @Test
    public void checkNegativeDiscountPercentage() throws Exception {
        // Test case to check handling of a negative discount percentage
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(4, "PERCENTAGE_OFF", -5, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, false); // Expecting failure for a negative discount percentage
    }

    @Test
    public void checkEmptySegmentsList() throws Exception {
        // Test case to check handling of an empty segments list
        List<String> segments = Collections.emptyList();
        OfferRequest offerRequest = new OfferRequest(5, "FLATY", 8, segments);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, false); // Expecting failure for an empty segments list
    }

    @Test
    public void checkNullSegmentsList() throws Exception {
        // Test case to check handling of a null segments list
        OfferRequest offerRequest = new OfferRequest(6, "BOGO", 3, null);
        boolean result = addOffer(offerRequest);
        Assert.assertEquals(result, false); // Expecting failure for a null segments list
    }

    public boolean addOffer(OfferRequest offerRequest) throws Exception {
        String urlString = "http://localhost:9001/api/v1/offer";
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");

        ObjectMapper mapper = new ObjectMapper();

        String POST_PARAMS = mapper.writeValueAsString(offerRequest);
        OutputStream os = con.getOutputStream();
        os.write(POST_PARAMS.getBytes());
        os.flush();
        os.close();
        int responseCode = con.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request did not work.");
        }
        return true;
    }
}
