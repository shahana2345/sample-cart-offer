import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.controller.OfferRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void checkFlatXForOneSegment_Success() throws Exception {
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(1, "FLATX", 10, segments);
        boolean result = addOffer(offerRequest);
        assertTrue(result);
    }

    @Test
    public void checkFlatXForMultipleSegments_Success() throws Exception {
        List<String> segments = Arrays.asList("p1", "p2", "p3");
        OfferRequest offerRequest = new OfferRequest(2, "FLATX", 20, segments);
        boolean result = addOffer(offerRequest);
        assertTrue(result);
    }

    @Test
    public void checkPercentageDiscountForOneSegment_Success() throws Exception {
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(3, "PERCENTAGE", 15, segments);
        boolean result = addOffer(offerRequest);
        assertTrue(result);
    }

    @Test
    public void checkInvalidOfferType_Failure() throws Exception {
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(4, "INVALID_TYPE", 10, segments);
        boolean result = addOffer(offerRequest);
        assertFalse(result);
    }

    @Test
    public void checkInvalidPercentageDiscount_Failure() throws Exception {
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(5, "PERCENTAGE", 120, segments);
        boolean result = addOffer(offerRequest);
        assertFalse(result);
    }

    @Test
    public void checkConnectionFailure_Failure() throws Exception {
        List<String> segments = Collections.singletonList("p1");
        OfferRequest offerRequest = new OfferRequest(6, "FLATX", 10, segments);

        // Mocking a scenario where the connection cannot be established
        mockConnectionFailure();

        boolean result = addOffer(offerRequest);
        assertFalse(result);
    }

    // Additional test cases can be added based on application's requirements

    private void mockConnectionFailure() throws IOException {
        throw new IOException("Connection failed");
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
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // print result
            System.out.println(response.toString());
        } else {
            System.out.println("POST request did not work.");
        }
        // return true;
        return responseCode == HttpURLConnection.HTTP_OK;
    }
}
