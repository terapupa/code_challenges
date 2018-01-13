package com.meetme;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

- Scan these items in this order: AAAA; Verify the total amount is $8.00.
- Scan these items in this order: ABCD; Verify the total amount is $15.40.

- Scan these items in this order: AAAA; Verify the total amount is $7.00.
- Scan these items in this order: AAAAA; Verify the total amount is $9.00.
- Scan these items in this order: AAAAAAAA; Verify the total amount is $14.00.
*/

public class MyCode {


    public static void main(String[] args) throws java.lang.Exception {
        System.out.println("Hello Java");


        Map<Character, Product> inventory = new HashMap<>();
        inventory.put('A', new Product()
                .addPrice(2.00f)
                .addPrice(4, 7.00f)
                .addPrice(7, 12.00f));
        inventory.put('B', new Product()
                .addPrice(12.00f));
        inventory.put('C', new Product()
                .addPrice(1.25f)
                .addPrice(6, 6.00f));
        inventory.put('D', new Product()
                .addPrice(0.15f)
                .addPrice(50, 5.00f)
                .addPrice(100, 90.00f));
        Terminal terminal = new Terminal(inventory);

        Cart cart = new Cart(terminal);

//        String test1 = "AAAAAAAAAAAABBBABBBBBD";
        String test1 = "AAAAAAAAA";

        for (Character item : test1.toCharArray()) {
            cart.scan(item);
        }
        System.out.println(cart.getTotal());
    }

    static class Terminal {

        private Map<Character, Product> inventory = new HashMap<>();

        public Terminal(Map<Character, Product> inventory) {
            if (inventory != null) {
                this.inventory = inventory;
            }
        }

        public Terminal() {
        }

        public Map<Character, Product> getInventory() {
            return inventory;
        }
    }

    static public class Product {

        TreeMap<Integer, Float> prices = new TreeMap<>();

        public Product() {
        }

        public Product addPrice(float price) {
            prices.put(1, price);
            return this;

        }

        public Product addPrice(int number, float price) {
            prices.put(number, price);
            return this;
        }

        public TreeMap<Integer, Float> getPrices() {
            return prices;
        }
    }

    static class Cart {

        private Terminal terminal = new Terminal();
        private Map<Character, Integer> cartMap = new HashMap<>();

        public Cart(Terminal terminal) {
            if (terminal != null) {
                this.terminal = terminal;
            }
        }

        public double getTotal() {

            float total = 0;
            for (Character key : cartMap.keySet()) {
                Product product = terminal.getInventory().get(key);
                if (product != null) {
                    total = total + getTotalPerProduct(terminal.getInventory().get(key), cartMap.get(key));
                }
            }
            return total;
        }

        public void scan(Character item) {

            if (cartMap.containsKey(item)) {
                cartMap.put(item, (cartMap.get(item) + 1));
            } else {
                cartMap.put(item, 1);
            }
        }

        private float getTotalPerProduct(Product product, int number) {
            float total = 0;

            for (Integer key : product.getPrices().descendingKeySet()) {
                int d = number / key;
                int m = number % key;
                if (d > 0) {
                    total = total + product.getPrices().get(key) * (number / key);
                    if (m > 0) {
                        total = total + getTotalPerProduct(product, number - key);
                    }
                    break;
                }
            }
            return total;
        }
    }


}
