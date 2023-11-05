# Module 0 : Building a simple checkout page

### Summary :

In this module, we will build a functional checkout page that will allow us to collect the information we need to make a payment request to Adyen. 

We will be intentionally using an older version of the library and web components. 

* The Java V18 library (see `build.gradle`).
* The adyen-web version 5.23.1 (see `layout.html`).

### Steps to implement :

1. Prepare your backend to receive the `CreateCheckoutSessionRequest` and return the `CreateCheckoutSessionResponse`.
    * In `CheckoutResource.java`, build a valid sessions request based on the information you have collected from the client-side.
        * You will need to set the return URL. We expect something of the format ".../redirect?orderRef=orderRef" (or equivalent)
          * The amount is set statically by you in the server side, for simplicity's sake. In a real scenario, they would come from your database.
        * The `applicationProperty` object contains useful information like your Merchant Account.
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
4. Test your integration
    * Run `./gradlew bootRun` to start the server, and open `http://localhost:8080/` in your browser. Complete a payment successfully to finish this module.
