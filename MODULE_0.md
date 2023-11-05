# Module 0 : Building a simple checkout page

## Briefing :

You're working as a full-stack developer for an E-commerce website that sells headphones and sunglasses in the Netherlands.
In fact, they sell the best headphones and sunglasses at 50.00 each and you're extremely excited to take on this challenge.
It's your job to implement the integration using Adyen and accept credit card payments, iDeal & klarna payments.

In this module, we will build a functional checkout page that will allow us to collect the information we need to make a payment request to Adyen.
For the sake of simplicity, we've hard-coded these items in the preview (cart) page.

We will be *intentionally* using an older version of the library and web components and work towards upgrading later on in the modules.

* The Java V18 library (see `build.gradle`).
* The adyen-web version 5.23.1 (see `layout.html`).

## Your job :

1. Prepare your backend to receive the `CreateCheckoutSessionRequest` and return the `CreateCheckoutSessionResponse`.
    * In `CheckoutResource.java`, build a valid sessions request based on the information you have collected from the client-side.
        * You will need to set the return URL. We expect something of the format ".../redirect?orderRef=orderRef" (or equivalent)
          * The amount is set statically by you in the server side, for simplicity's sake. In a real scenario, they would come from your database.
        * The `applicationProperty` object contains useful information like your Merchant Account.
        * Make sure that your request contain an idempotency key.
2. Prepare your backend to receive the necessary webhook that will be triggered after the payment is completed.
    * In `Webhookresource.java`, build the logic to handle the `NotificationRequest` incoming, and print some useful information on the screen
        * Validate the `hmacKey`, you can use the `this.applicationProperty.getHmacKey()` helper function
        * Print the Merchant Reference, Alias and PSP reference contained in the notification.
        * Don't forget that you should return  `"[accepted]"` in your body.
        * _Note: Don't forget to create a new Standard webhook to receive data! In the Customer Area under the Developers → Webhooks section._ See [the documentation](https://docs.adyen.com/development-resources/webhooks/) and  [this article](https://github.com/adyen-examples/.github/blob/main/pages/webhooks-testing.md) for more information on how to receive webhooks locally.
3. Prepare your frontend to instantiate the session, and send valid information to your server
    * In `layout.html` add the necessary imports for the library and web components. We will intentionally use the old version [Web Components/Drop-in v5.23.1](https://docs.adyen.com/online-payments/release-notes/?integration_type=web&tab=embed-script-and-stylesheet_2022-08-30-uzt4_2#releaseNote=2022-08-29-web-componentsdrop-in-5.23.1)
    * In this workshop, we have chosen for a fully static, Javascript implementation for simplicity. All the implementation is contained in a single, `adyenImplementation.js` file.
    * In `adyenImplementation.js`, you will have to do a few things. 
      * Call the `/api/sessions` endpoint in the `startCheckout` function to start handling the session.
      * Complete the configuration of the `AdyenCheckout` object in the `createAdyenCheckout` function.
      * Note that [if you are selecting a payment method that needs a redirect](https://docs.adyen.com/online-payments/build-your-integration/?platform=Web&integration=Components&version=5.53.2#handle-the-redirect), the `finalizeCheckout` method will be called with a `sessionId` value. You do not need to do anything for this.
      * Show the amount that the shopper needs to pay on the drop-in.
4. Add `klarna` to the list of available payment methods
    * In the customer area, make sure Klarna is activated
    * In `index.html`, add a new list element featuring `type=klarna` to be ble to test the klarna component in your application
    * _Note there are actually 3 Klarna payment method types klarna_account, klarna and klarna_paynow. Pick one of your liking._
    * Modify the `adyenImplementation.js` and `CheckoutResource.java` code to support the new payment method.
5. Add `ideal` to the list of available payment methods
    * In the customer area, make sure ideal is activated
    * In `index.html`, add a new list element featuring `type=ideal` to be ble to test the klarna component in your application
    * Modify the `adyenImplementation.js` and `CheckoutResource.java` code to support the new payment method.
6. Test your integration
    * Run `./gradlew bootRun` to start the server, and open `http://localhost:8080/` in your browser. 
    * Complete a payment successfully using card, ideal and klarna to finish this module.
