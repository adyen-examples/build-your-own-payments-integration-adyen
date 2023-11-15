# Module 4 : Adding gift cards

* You can keep using your own code to implement this module.
* A clean starting template is available at all times with the `module-4-start` tag.
    * `git checkout module-4-start` to get the starting template.
* A proposed solution is available with the `module-4-end` tag.
    * `git checkout module-4-end` to get the proposed solution.

## Briefing

We've noticed that some of our customers would love to give their friends some nice headphones or sunglasses as a gift.
They will have to order it through the website themselves. We want to make sure that they can pay with a gift card.

## Your job :

0. Enable the gift cards payment methods in the Customer Area.
  * *Note:* We've added some extra styling properties in `application.css` to support gift cards
1. Prepare your backend to handle gift cards, see [documentation](https://docs.adyen.com/payment-methods/gift-cards/).
  * You'll need to implement the three methods  in the `OrderResource.java` controller
    * `/balanceCheck` method calls the `/paymentMethods/balance`-endpoint
    * `/cancelOrder` method calls the `/cancelOrder`-endpoint
    * `/createOrder` method calls the `/order/`-endpoint
  * In the `/initiatePayment`, we'll have to handle partial orders
    * In the case of a partial order, we need to keep track of a `remainingAmount` that the shopper needs to pay (use the `OrderService.java` service to save this in a session)
    * **Tip:** if the incoming payment method is a gift card, we'll need to a balanceCheck again to verify whether the gift card has sufficient funds.
     You can implement a helper function that takes in a paymentMethod in `OrderService.java` (e.g. `getAmountFromGiftCard(paymentMethod)`) and returns the amount
2. Prepare your backend to handle the respective gift card webhooks.
    * Validate the HMAC signature of the incoming webhooks.
    * Print or log the amount values that the ORDER_CLOSED webhook contains in the additionalData property.
    * Make sure to handle the AUTHORISATION webhook, ORDER_OPENED and ORDER_CLOSED webhooks accordingly.
3. Prepare the frontend to handle gift cards.
    * When using drop-in, partial payments are handled within the drop-in component.
    * When using components, you'll have to handle the remaining amount yourself by overriding the respective event handlers `onBalanceCheck`, `onOrderRequest` and `onOrderCancel`.
      These can be found in `adyenGiftCardsImplementation.js` -> `giftCardConfiguration`.
4. Perform a successful gift card (partial) payment, finish this module by completing a gift card **and** a debit card/iDEAL partial payment to pay the remaining amount. 
