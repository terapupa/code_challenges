package com.meetme;

/*
### Description
Consider a store where each item has a amount per unit. For example, apples may be $1.00 each.
Implement a point-of-sale scanning API that accepts an arbitrary ordering of products
(similar to what would happen at a checkout line) and then returns the correct total
amount for an entire shopping cart based on the per unit prices as applicable.
Here are the products listed by code and the prices to use (there is no sales tax):
|Product Code | Prices                           |
|------------:|:---------------------------------|
|A            | $2.00, 4 units of A it will be 7 |
|B            | $12.00                           |
|C            | $1.25 or $6 for a six pack       |
|D            | $0.15 x1, 50 x 5, 90 x           |
For your solution, we ask that you use Java.
There should be a top level point of sale terminal service object or namespace that looks something like the pseudo-code below.
You are free to design and implement the rest of the code however you wish, including how you specify the prices in the system:
```
terminal.setPricing(...)
terminal.scan("A")
terminal.scan("C")
... etc.
result = terminal.total
```
Here are the minimal inputs you should use for your test cases. These test cases must be shown to work in your program:
- Scan these items in this order: AAAAA; Verify the total amount is $9.00.
- Scan these items in this order: ABCD; Verify the total amount is $15.40.
- Scan these items in this order: AAAA; Verify the total amount is $7.00.
- Scan these items in this order: AAAAA; Verify the total amount is $9.00.
- Scan these items in this order: AAAAAAAA; Verify the total amount is $14.00.
*/

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ShoppingCart {

  public static void main(String[] args) {
    Terminal terminal = new Terminal();
    terminal.setPricing("A", 1, 2.00f);
    terminal.setPricing("A", 4, 7.00f);
    terminal.setPricing("B", 1, 12.00f);
    terminal.setPricing("C", 1, 1.25f);
    terminal.setPricing("C", 6, 6f);
    terminal.setPricing("D", 1, 0.15f);
    terminal.setPricing("D", 50, 5f);

//    terminal.scan("A");
//    terminal.scan("B");
//    terminal.scan("C");
//    terminal.scan("D");
    terminal.scan("A");
    terminal.scan("A");
    terminal.scan("A");
    terminal.scan("A");
    terminal.scan("A");
//    terminal.scan("A");
//    terminal.scan("A");
//    terminal.scan("A");

    System.out.println("Total : $" + terminal.getTotal());
  }

  static class Terminal {
    private final Map<String, NavigableMap<Integer, Float>> priceMap = new HashMap<>();
    private final Map<String, Integer> scannedItems = new HashMap<>();

    public void setPricing(String product, int quantity, float price) {
      NavigableMap<Integer, Float> productPrice = priceMap.get(product);
      if (productPrice == null) {
        productPrice = new TreeMap<>();
      }
      productPrice.put(quantity, price);
      priceMap.put(product, productPrice);
    }

    public void scan(String product) {
      scannedItems.merge(product, 1, Integer::sum);
    }

    public float getTotal() {
      float totalCost = 0;
      for (String product : scannedItems.keySet()) {
        if (priceMap.containsKey(product)) {
          Integer productItems = scannedItems.get(product);
          while (productItems > 0) {
            NavigableMap<Integer, Float> priceEntries = priceMap.get(product);
            Integer closedPriceKey = priceEntries.floorKey(productItems);
            totalCost = totalCost + priceEntries.get(closedPriceKey) * (float) (productItems / closedPriceKey);
            productItems = productItems % closedPriceKey;
          }
        }
      }
      return totalCost;
    }

  }
}
