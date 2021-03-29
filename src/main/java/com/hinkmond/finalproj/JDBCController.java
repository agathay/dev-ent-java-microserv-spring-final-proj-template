package com.hinkmond.finalproj;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.*;

import java.util.*;
import java.io.*;


@RestController
public class JDBCController {
    private final static String KEYFILEPATH = "./keyFile.key";

    @CrossOrigin
    @RequestMapping(value = "/helloworld", method = RequestMethod.GET)
    public String printCryptTest() {
        AESUtils aesUtils = new AESUtils();

        String encryptedStr = aesUtils.encrypt("Hello World!", KEYFILEPATH);
        return ("Decrypt = " + aesUtils.decrypt(encryptedStr, KEYFILEPATH));
    }

    @CrossOrigin
    @SuppressWarnings("SqlResolve")
    @RequestMapping(value = "/printAllCrypto", method = RequestMethod.GET)
    public String printAllUsers() {
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        StringBuilder resultStr = new StringBuilder();

        String queryStr = "SELECT * from crypto_info;";
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryStr);
        while (sqlRowSet.next()) {
            resultStr.append(sqlRowSet.getString("symbol")).append(" -- ")
                    .append(sqlRowSet.getString("name")).append(", ")
                    .append('$' + sqlRowSet.getString("price")).append(" Updated: ")
                    .append(sqlRowSet.getString("updated_at"))
                    .append("\n");
        }
        return ("" + resultStr);
    }

    @CrossOrigin
    @SuppressWarnings("SqlResolve")
    @RequestMapping(value = "/updateCryptoPrice", method = RequestMethod.POST)
    public String updateLatest(@RequestBody UpdateCryptoData updateCryptoData) throws IOException {
        // https://nomics.com/docs/

        String crypto = updateCryptoData.getSymbol();
        // Only BCH, BTC, ETH, LTC are supported in this app.
        if (!(crypto.equals("BCH") || crypto.equals("BTC") || crypto.equals("ETC")|| crypto.equals("ETH") || crypto.equals("LTC"))) {
            String reasonMsg = "Only BCH -- Bitcoin Cash, BTC -- Bitcoin, ETC -- Ethereum Classic, ETH -- Ethereum and LTC -- Litecoin are supported in this app.";
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, reasonMsg);
        }
        URL url = new URL("https://api.nomics.com/v1/currencies/sparkline?key=8427cbef660536f213a6b2d5706a4a27&ids="
                + crypto + "&start=2021-03-25T00%3A00%3A00Z");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("accept", "application/json");

        InputStream responseStream = connection.getInputStream();

        // Read from crypto prices datafeed
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = responseStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] byteArray = buffer.toByteArray();

        // Transform to Json
        String jsonString = new String(byteArray, java.nio.charset.StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString.substring(1, jsonString.length() - 1));

        // Update price in DB
        List<String> list = mapper.convertValue(rootNode.get("prices"), ArrayList.class);
        String price = list.get(list.size() - 1);
        JdbcTemplate jdbcTemplate = JDBCConnector.getJdbcTemplate();
        String queryStr = "UPDATE crypto_info SET price = " + price + " WHERE symbol = '" + crypto + "'";
        int rowsUpdated = jdbcTemplate.update(queryStr);

        return crypto + " has been updated to price " + price;
    }
}
