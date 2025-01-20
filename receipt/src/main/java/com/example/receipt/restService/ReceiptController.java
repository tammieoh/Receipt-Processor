package com.example.receipt.restService;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.receipt.Receipt.Receipt;

@RestController
public class ReceiptController {

    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService service) {
        this.receiptService = service;
    }

    @PostMapping("/receipts/process")
    public ResponseEntity<String> processReceipt(@Validated @RequestBody Receipt receipt) {
        return receiptService.addNewReceipt(receipt);
    }

    @GetMapping("/receipts/{id}/points")
    public ResponseEntity<String> getReceiptPoints(@PathVariable("id") String id) {
        return receiptService.getPoints(id);
    }
}
