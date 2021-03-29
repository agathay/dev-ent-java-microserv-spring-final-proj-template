**Show Cryptocurrencies**
----
Returns 5 cryptocurrencies information in your database.

* **URL**

  /printAllCrypto

* **Method:**

  `GET`

*  **URL Params**

   None

* **Data Params**

  None

* **Success Response:**

    * **Code:** 200 <br />
      **Content:** `BCH -- Bitcoin Cash, $515.01 Updated: 2021-03-28 22:41:56.0 BTC -- Bitcoin, $55311.53 Updated: 2021-03-28 22:41:30.0 ETC -- Ethereum Classic, $12.36 Updated: 2021-03-28 22:42:50.0 ETH -- Ethereum, $1683.68 Updated: 2021-03-28 22:41:36.0 LTC -- Litecoin, $192.13 Updated: 2021-03-28 22:41:46.0`

* **Error Response:**

    * **Code:** 500 Internal Server Error <br />
 
* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/printAllCrypto",
      dataType: "text",
      type : "GET",
      success : function(r) {
        console.log(r);
      }
    });
  ```

**Update crypto price from datafeed**
----
Update a particular crypto price from public datafeed.

* **URL**

  /updateCryptoPrice/:symbol

* **Method:**

  `POST`

*  **URL Params**

   **Required:**

   `symbol=[BCH|BTC|ETC|ETH|LTC]`

* **Data Params**

  None

* **Success Response:**

    * **Code:** 200 <br />
      **Content:** `ETH has been updated to price 1811.15440471`

* **Error Response:**

    * **Code:** 406 Not Acceptable <br />
      **Content:** `Only BCH -- Bitcoin Cash, BTC -- Bitcoin, ETC -- Ethereum Classic, ETH -- Ethereum and LTC -- Litecoin are supported in this app`

  OR

    * **Code:** 500 Internal Server Error <br />
    
* **Sample Call:**

  ```javascript
    $.ajax({
      url: "/updateCryptoPrice",
      dataType: "text",
      type : "POST",
      success : function(r) {
        console.log(r);
      }
    });
  ```
