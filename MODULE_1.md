# Module 1 : Building an advanced checkout page using the advanced flow

## Briefing

It turns out hard-coding the amount was not the best idea.
Now, every time a customer **has** to buy a pair of headphones and sunglasses. Not very ideal :), shoppers should be able to add items and the amount needs to update accordingly.
One option is to use the `/sessions`-endpoint, and create a new sessionId and merchantReference for every change. This is too heavy for our use case.
The other option is to rework our implementation and use advanced checkout now. Let's look at the [documentation](https://docs.adyen.com/online-payments/build-your-integration/additional-use-cases/advanced-flow-integration/).

## Your job :

In this module, we will transform our sessions implementation into an advanced checkout page using the advanced flow. Here are some helpful tips:
The `/sessions` calls the following three Adyen endpoints: [1] `/paymentmethods` (retrieves available payment methods), [2] `/payments` (starts a transaction) and [3] `/payments/details` (submits payment details).
This means we'll have to change a couple of things on our front- and backend.
* Frontend: We need to override several event handlers and handle the subsequent calls.
    * Show the amount that the shopper needs to pay on the drop-in.
* Backend `/api/CheckoutResource.java`: We have to implement these three calls that the frontend needs to call. We'll also need include additional parameters.
    * The `initiatePayment` method should have a variable amount that can be changed according to the shopper's cart.
    * The `getPaymentMethods` and `submitAdditionalDetails` should be implemented in `CheckoutResource.java`.
    * Tip: use a session cookie to temporarily store the items or an in-memory cache.
* _Note: For redirects during a payment (returnUrl), we'll have to handle this accordingly in `/handleShopperRedirect`_
