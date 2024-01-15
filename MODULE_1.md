# Module 1 : Building an advanced checkout page using the advanced flow

* You can keep using your own code to implement this module.
* A clean starting template is available at all times with the `module-1-start` tag.
    * `git checkout module-1-start` to get the starting template.
* A proposed solution is available with the `module-1-end` tag.
    * `git checkout module-1-end` to get the proposed solution.

## Briefing

It turns out hard-coding the amount was not the best idea.
Now, every time a customer **has** to buy a pair of headphones and sunglasses. Not very ideal :), shoppers should be able to add items and the amount needs to update accordingly.
One option is to use the `/sessions`-endpoint, and create a new sessionId and merchantReference for every change. This is too heavy for our use case.
The other option is to rework our implementation and use advanced checkout now. Let's look at the [documentation](https://docs.adyen.com/online-payments/build-your-integration/additional-use-cases/advanced-flow-integration/).

## Your job :

In this module, we will transform our sessions implementation into an advanced checkout page using the advanced flow. 

1. In order to have a dynamic cart functionality available, we have added a `CartResource`. 
    * This additional API is triggered inside the `preview.html` template and keeps track of the current cart.
    * The cart is stored in a session cookie.
    * Ignore the actual implementation of the functionality, the point here is to have to use dynamic checkout. It's implemented so you can use functions like `getTotalAmount()` to retrieve the amount dynamically.
2. Convert the previous `/sessions` implementation to use the advanced flow instead.
    * In `/api/CheckoutResource.java`, implement the `initiatePayment`, `getPaymentMethods` and `submitAdditionalDetails` methods.
    * Don't forget to use the `CartResource` to retrieve the content of the cart as well as the final amount.
    * _Note: For redirects during a payment (returnUrl), we'll have to handle this accordingly in `/handleShopperRedirect?orderRef=..."`_
    * Complete `adyenImplementation.js`, using the previous implementation as a reference.
3. This module is successful when the website works just as before. Test the different payments methods to make sure everything is working as expected.