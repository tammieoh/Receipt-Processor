package com.example.receipt.restService;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.receipt.Receipt.Item;
import com.example.receipt.Receipt.Receipt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReceiptService {

    private static HashMap<String, Receipt> receiptMap = new HashMap<>();
    private static ObjectMapper objectMapper;

    public ReceiptService(HashMap<String, Receipt> receiptMap, ObjectMapper objectMapper) {
        this.receiptMap = receiptMap;
        this.objectMapper = objectMapper;
    }

    static HashMap<String, String> prepJsonResponse(String key, String value) {
        HashMap<String, String> response = new HashMap<>();
        response.put(key, value);

        return response;
    }

    static ResponseEntity<String> addNewReceipt(Receipt receipt) {
        String id = UUID.randomUUID().toString();

        if (receiptMap.get(id) == null) {
            receiptMap.put(id, receipt);
        }

        try {
            String jsonResponse = objectMapper.writeValueAsString(prepJsonResponse("id", id));
            return ResponseEntity.ok(jsonResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("This receipt is invalid");
        }
    }

    static ResponseEntity<String> getPoints(String id) {
        Receipt receipt = receiptMap.get(id);

        int sumPoints = 0;

        if (receipt == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No receipt found for that ID.");
        }

        int retailerPoints = calculateRetailerPoints(receipt.getRetailer());

        // get .00 cents points
        int centPoints = checkCents(receipt.getTotal());

        // get .25 cent multiple points
        int multiplePoints = checkMultiple(receipt.getTotal());

        // get items of two points
        int itemsTwoPoints = itemsOfTwo(receipt.getItems());

        // get item desc multiple of 3 points
        int itemDescPoints = trimDescriptions(receipt.getItems());

        // get odd date points
        int oddDayPoints = oddPurchaseDate(receipt.getPurchaseDate());

        // get timePoints
        int timePoints = timeInterval(receipt.getPurchaseTime());

        sumPoints = retailerPoints + centPoints + multiplePoints + itemsTwoPoints
                + itemDescPoints + oddDayPoints + timePoints;

        try {
            String jsonResponse = objectMapper
                    .writeValueAsString(prepJsonResponse("points", String.valueOf(sumPoints)));
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    private static int calculateRetailerPoints(String retailerName) {
        int points = 0;
        for (int i = 0; i < retailerName.length(); i++) {
            char a = retailerName.charAt(i);
            if (Character.isAlphabetic(a) || Character.isDigit(a)) {
                points++;
            }
        }
        return points;
    }

    private static int checkCents(String total) {
        Double amount = Double.parseDouble(total);
        int dollars = (int) (amount * 100);
        if (dollars % 100 == 0) {
            return 50;
        }
        return 0;
    }

    private static int checkMultiple(String total) {
        Double amount = Double.parseDouble(total);

        if (amount % 0.25 == 0) {
            return 25;
        }
        return 0;
    }

    private static int itemsOfTwo(Item[] items) {
        return 5 * (items.length / 2);
    }

    private static int trimDescriptions(Item[] items) {
        int itemPointsSum = 0;
        for (Item i : items) {
            int descLen = i.getShortDescription().trim().length();
            if (descLen % 3 == 0) {
                Double price = Double.parseDouble(i.getPrice());
                int itemPrice = (int) Math.ceil(price * 0.2);
                itemPointsSum += itemPrice;
            }
        }
        return itemPointsSum;
    }

    private static int oddPurchaseDate(String date) {
        String[] dateSplit = date.split("-");
        int day = Integer.parseInt(dateSplit[2]);
        if (day % 2 != 0) {
            return 6;
        }
        return 0;
    }

    private static int timeInterval(String timeStamp) {
        int time = Integer.parseInt(timeStamp.replaceAll(":", ""));
        if (time >= 1400 && time < 1601) {
            return 10;
        }
        return 0;
    }
}
