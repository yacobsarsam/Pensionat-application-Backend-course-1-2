package com.example.pensionat.IT;

import com.example.pensionat.Services.XmlURLProvider;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ShppersIT {
    XmlURLProvider xmlURLProvider =new XmlURLProvider();
    @Test
    void GetCompanyCustomersWillFetch() throws IOException {
        Scanner s = new Scanner(xmlURLProvider.GetCompanyCustomersURL().openStream()).useDelimiter("//A");
        String result = s.hasNext() ? s.next() : "";
        System.out.println("Result från inläsning XML-avtalsKunder : " + result);
        assertTrue(result.contains("customers"));
        assertTrue(result.contains("/customers"));
        assertTrue(result.contains("id"));
        assertTrue(result.contains("/id"));
        assertTrue(result.contains("companyName"));
        assertTrue(result.contains("/companyName"));
        assertTrue(result.contains("contactName"));
        assertTrue(result.contains("/contactName"));
        assertTrue(result.contains("contactTitle"));
        assertTrue(result.contains("/contactTitle"));
        assertTrue(result.contains("streetAddress"));
        assertTrue(result.contains("/streetAddress"));
        assertTrue(result.contains("city"));
        assertTrue(result.contains("/city"));
        assertTrue(result.contains("postalCode"));
        assertTrue(result.contains("/postalCode"));
        assertTrue(result.contains("country"));
        assertTrue(result.contains("/country"));
        assertTrue(result.contains("phone"));
        assertTrue(result.contains("/phone"));
        assertTrue(result.contains("fax"));
        assertTrue(result.contains("/fax"));
        assertTrue(result.contains("/customers"));

    }
}