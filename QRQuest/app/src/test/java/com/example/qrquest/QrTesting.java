package com.example.qrquest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QrTesting {
    private QRCode mockQrCode(){

        QRCode qrCode = new QRCode(value());
        return qrCode;
    }
    private String value(){
        return "BFG5DGW54";
    }
    @Test
    void testHash(){
        QRCode qrCode = mockQrCode();
        assertEquals(111, qrCode.getScore());
    }

}
