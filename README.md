# Crypto Exchange API

This application provides two main endpoints for retrieving cryptocurrency exchange rates from CoinGecko and performing exchanges with a 1% fee. Below is comprehensive documentation in **Markdown** format.

---

## Table of Contents
- [Overview](#overview)
- [Prerequisites](#prerequisites)
- [Running the Application](#running-the-application)
- [Endpoints](#endpoints)
    - [GET /currencies/{base}](#get-currenciesbase)
    - [POST /currencies/exchange](#post-currenciesexchange)
- [Notes](#notes)
---

## Overview

- **Purpose**:
    - Fetch current crypto rates from CoinGecko.
    - Perform currency exchanges with a 1% fee taken from the “from” amount.
- **Technologies**:
    - Java (Spring Boot, Lombok),
    - Maven for build,
    - REST Endpoints,
    - CoinGecko API as the external data provider.

---

## Prerequisites

1. **Java**: Make sure you have at least Java 11 (or whatever version your project requires).
2. **Maven**: Installed and configured properly if you want to build/run via Maven.(May need to add framework support after repo clone)
3. **Lombok**: Ensure your IDE supports Lombok annotation processing if you’re reading the code.

---

## Running the Application

1. **Clone the project**:
   ```bash
   git clone https://github.com/your-repo/crypto-exchange.git
   cd crypto-exchange

2. **Build the Project (Maven example)**:
   ```bash
   mvn clean install


3. **Run**:
   ```bash
   mvn spring-boot:run

4. **Check Logs**:

 - Spring Boot may log any random generated security password (if default security is enabled).
 - For a different port or other overrides, see application.yaml or application.properties.

# Currency Exchange API

This document describes the available endpoints, request/response formats, and error handling for the Currency Exchange API.

---

## Endpoints

### 1. GET `/currencies/{base}`

#### Description
Retrieves the current exchange rates for a given base coin.

- **Path Variable**
    - `{base}`: The coin name recognized by the system (e.g., `"bitcoin"`, `"ethereum"`).

- **Query Parameters** (optional)
    - `filter[]` (repeated): An array of coin IDs such as `btc`, `eth`, `usd`, or `pln`. These limit which rates are returned.

#### Request Format

    GET /currencies/{base}?filter[]=coinId&filter[]=coinId2 ...

#### Example:

    GET /currencies/bitcoin?filter[]=btc&filter[]=eth
- base = bitcoin
- filters = [usd, eth]

#### Example Response:

            {
              "source": "bitcoin",
              "rates": {
                "usd": 101644,
                "eth": 31.811758
              }
            }

- source: The base coin name.
- rates: A map of { targetCoinId -> exchangeRate }. 

#### Possible HTTP Status Codes

- 200 OK: Successful retrieval, JSON rates returned.
- 400 Bad Request: Invalid input (missing base symbol or filters).
- 500 Internal Server Error: Unexpected errors or external API issues.

### 2. POST /currencies/exchange
   
#### Description
   
Performs a currency exchange from one coin to multiple target coins, applying a 1% fee on the “from” amount.

- **Request Body Fields**
    - `from` (string): Base coin name, e.g. "bitcoin".
    - `to` (array of strings): Each element is a coin ID, e.g. ["eth", "pln"].
    - `amount` (number): How many “from” coins to exchange before applying the 1% fee.

#### Request format:

    POST /currencies/exchange
    Content-Type: application/json
    
    {
        "from": "bitcoin",
        "to": ["eth", "pln"],
        "amount": 2.0
    }

#### Response

    {
        "from": "bitcoin",
        "conversions": {
            "usd": {
                "rate": 27702.34,
                "amount": 1.0,
                "result": 27425.3166,
                "fee": 0.01
            },
            "eth": {
                "rate": 15.33,
                "amount": 1.0,
                "result": 15.1767,
                "fee": 0.01
            }
        }
    }


### Notes
#### Base Coin vs. Coin IDs

- {base} in the path is a long name like "bitcoin" or "ethereum".
- Filters, from and the toArray use short IDs (lower and uppercased) like btc, eth, usd, pln, etc.

#### CoinGecko Integration
The app queries CoinGecko’s /simple/price or related endpoints.
Make sure your application.yaml or application.properties sets coingecko.api.key.
