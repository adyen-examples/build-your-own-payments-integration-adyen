# Module 4 : Adding gift cards

* You can keep using your own code to implement this module.
* A clean starting template is available at all times with the `module-4-start` tag.
    * `git checkout module-4-start` to get the starting template.
* A proposed solution is available with the `module-4-end` tag.
    * `git checkout module-4-end` to get the proposed solution.

## Briefing

We've noticed that some of our customers would love to give their friends some nice headphones as a gift.
They will have to order it through the website themselves. We want to make sure that they can pay with a gift card.

## Your job :

0. Enable the gift cards payment method in the Customer Area.
1. Prepare your backend to handle gift cards, see [documentation](https://docs.adyen.com/payment-methods/gift-cards/).
2. Prepare your backend to handle the respective gift card webhooks.
    * Validate the HMAC signature of the incoming webhooks.
    * Print or log the amount values that the ORDER_CLOSED webhook contains in the additionalData property.
    * Make sure to handle the AUTHORISATION webhook, ORDER_OPENED and ORDER_CLOSED webhooks accordingly.
3. Prepare the frontend.
    * When using drop-in, partial payments are handled within the drop-in component.
    * When using components, you'll have to handle the remaining amount yourself by overriding the handler.
4. Perform a successful gift card (partial) payment, can you use a gift card **and** a debit card payment to pay the remaining amount?
