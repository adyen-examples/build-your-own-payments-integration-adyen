# Build your own Payment Integration with Adyen

## Introduction

This repository is a step-by-step guide to building your own payment integration with Adyen. We will start from a simple drop-in integration, and build our way up to a fully customized checkout page.

Each module starts at the relevant tag number. For example, to start module 2, checkout tag `module-2-start`.
In case you want to skip a module or compare your solution, you can checkout the corresponding tag. For example, `module-2-end` for module 2.

_Note that the workshop is intended to highlight some of the pains of our customers when building and upgrading. We know there might be better ways to do things, with a deeper knowledge of the platform._

The list of modules is as such : 

* Module 1 : Building a simple checkout page using sessions.

### Context of the code repository.

In this workshop, we are using Java and Spring Boot, together with a static frontend based on thymelead template.
We use those because we want to reduce the amount of prerequisite knowledge (like a frontend framework) and use a strongly typed language for the backend to build empathy for the customer experience.

In this workshop we are not asking you to build a complete integration, but rather to fill in the voids based on resources you can find in our docs and other online resources.

Some information on how the project is constructed :
* The java code is to be found in `src/main/java/com/adyen/checkout`
  * The code you need to update is in the `api` folder. `CheckoutResource.java` for API related code and `WebhookResource.java` for webhooks.
  * You can access environment variables via the `applicationProperty` object.
* The frontend templates are to be found in `src/main/resources/templates` and the static resources in `src/main/resources/static`
  * The `templates` contains the html files that are rendered by the server.
  * The `layout.html` file is the template file that contains the adyen `js` and `css` imports.
  * The `adyenImplementation.js` file from the `static` folder is where the client related logic is located.
* Some additional information: 
  * The `clientKey` is automatically passed from the backend to the client side via a hidden `clientKey` field for convenience.
  * In order to play around with multiple payment methods, a `type` value is passed from the client to the server, which contains the name of an adyen payment method and that the adyen web components will recognize. You will not need to interact with this but we are mentioning it to avoid any confusion :).
* To run the project 
  * `./gradlew bootRun` will start the server on port 8080.
  * `./gradlew build` will build the project.
  * You can also run the project from your IDE, like IntelliJ or Eclipse. The main class is then `src/main/java/com/adyen/checkout/OnlinePaymentsApplication.java`.

### Prerequisites

- Set the following environment variables on your system: `ADYEN_MERCHANT_ACCOUNT`, `ADYEN_API_KEY`, `ADYEN_HMAC_KEY`, `ADYEN_CLIENT_KEY`.

on MacOS, it looks like this:
```bash
export ADYEN_API_KEY="API_KEY"
export ADYEN_CLIENT_KEY="CLIENT_KEY"
export ADYEN_MERCHANT_ACCOUNT="MERCHANT_ACCOUNT"
export ADYEN_HMAC_KEY="HMACKEY"
```

## Module 0: Building a simple checkout page.

### Briefing:
You're working as a full-stack developer for an E-commerce website that sells socks in the Netherlands.
In fact, they sell the best socks at 10.99$ each and you're extremely excited for Project #SOCKS.
It's your job to do the integration with Adyen and accept credit card payments, iDeal and klarna payments.

In this module, we will build a functional checkout page that will allow us to collect the information we need to make a payment request to Adyen. 

We will be *intentionally* using an older version of the library and web components and work towards upgrading later on in the modules.

* The Java V18 library (see `build.gradle`).
* The adyen-web version 5.23.1 (see `layout.html`).


1. Prepare your backend to receive the `CreateCheckoutSessionRequest` and return the `CreateCheckoutSessionResponse`.
    * In `CheckoutResource.java`, build a valid sessions request based on the information you have collected from the client-side.
        * You will need to set the return URL. We expect something of the format "http://localhost:8080/redirect?orderRef=orderRef" (or equivalent).
          * The amount can be hard coded on the server side, for simplicity's sake. In a real scenario, they would come from your database.
        * The `applicationProperty` object contains useful information like your Merchant Account.
        * Make sure that your request contain an idempotency key. // ?
2. Prepare your backend to receive the necessary webhook that will be triggered after the payment is completed.
    * In `Webhookresource.java`, build the logic to handle the `NotificationRequest` incoming, and print some useful information on the screen
        * Validate the `hmacKey`, you can use the `this.applicationProperty.getHmacKey()` helper function
        * Print the Merchant Reference, Alias and PSP reference contained in the notification.
        * Don't forget that you should return  `"[accepted]"` in your body.
        * _Note: Don't forget to create a new Standard webhook to receive data! In the Customer Area under the Developers → Webhooks section._ See [documentation](https://docs.adyen.com/development-resources/webhooks/) for more information.
3. Prepare your frontend to instantiate the session, and send valid information to your server.
    * In `layout.html` add the necessary imports for the library and web components. We will intentionally use the old version [Web Components/Drop-in v5.23.1](https://docs.adyen.com/online-payments/release-notes/?integration_type=web&tab=embed-script-and-stylesheet_2022-08-30-uzt4_2#releaseNote=2022-08-29-web-componentsdrop-in-5.23.1)
    * In `adyenImplementation.js`, we will have to do a few things. 
      * Call the `/api/sessions` endpoint in the `startCheckout` function to start handling the session.
      * Handle the response by redirecting the user to the correct page
      * Complete the configuration of the `AdyenCheckout` object in the `createAdyenCheckout` function.
      * Note that if you are selecting a payment method that needs a redirect, the `finalizeCheckout` method will be called with a `sessionId` value. You do not need to do anything for this.
4. Test your integration
    * Run `./gradlew bootRun` to start the server, and open `http://localhost:8080/` in your browser
5. Add a payment method
    * Add klarna (pay now, pay later and pay in installments) as a payment method in the Customer Area
    * Complete a klarna payment
    * Add iDeal as a payment method in the Customer Area
    * Complete an iDeal payment
    * Finally, complete a credit-card payment successfully to finish this module.

## Module 1: Adding additional features & Advanced flow

### Briefing:
Project #SOCKS has been very successful. Not every one in the world can keep their feet warm,
that's why the company decides to partner with their favorite charity and allow their customers to donate after every successful sock purchase!

1. Prepare your backend to handle an Adyen giving flow: https://docs.adyen.com/online-payments/donations/web-component/
  * You'll notice that you have to change your existing backend and frontend flows.
  * The `/sessions` calls the following three Adyen endpoints: [1] `/paymentmethods` (retrieves available payment methods), [2] `/payments` (starts a transaction) and [3] `/payments/details` (submits payment details).
  This means we'll have to change a couple of things on our front- and backend according to: https://docs.adyen.com/online-payments/build-your-integration/additional-use-cases/advanced-flow-integration/, here are some helpful tips:
    * Frontend: We need to override several event handlers and handle the subsequent calls.
    * Backend `/api/CheckoutResource.java`: We have to implement these three calls that the frontend needs to call. We'll also need include additional parameters.

2. Prepare your backend to handle the incoming donation webhook.
    * In `Webhookresource.java`, build the logic to handle the `NotificationRequest` incoming, and print some useful information on the screen.
    * Validate the HMAC signature of the webhook.
    * Tip: Notice that the webhook is different and that you'll have to create a different endpoint to receive this webhook.

3. Update your frontend so that it contains a donation screen that calls your backend when a donation-amount is specified.
    * Using Adyen Components, add and mount the `<div>` accordingly. The button should call your respective endpoint that you've implemented in step 1.
    * Tip: You can save the donationToken/pspReference in your cookie session.

4. Perform a successful donation to finish this module and make sure to receive the webhook.

**Tip:** You need to enable donations in the Customer Area / Backoffice.


## Module 2: Adding gift cards

### Briefing: We've noticed that some of our customers would love to give their friends some nice socks as a gift. But we'd like the friends to choose their own favorite socks.

1. Prepare your backend to handle gift cards: https://docs.adyen.com/payment-methods/gift-cards/

2. Prepare your backend to handle the respective gift card webhooks.
   * Validate the HMAC signature of the incoming webhooks
   * Print the amounts that the ORDER_CLOSED webhook has

3. Prepare the frontend
   * When using drop-in, partial payments are handled within the drop-in component.
   * When using components, you'll have to handle the remaining amount yourself by overriding the respective handler

4. Perform a successful gift card (partial) payment, can you use a gift card **and** a debit card payment to pay the remaining amount?

** Tip: ** Yuu need to enable the gift cards payment method in the Customer Area.


## Module 3: Upgrading to the latest version of the library and Adyen Dropin/Web components.

1. Upgrade your Java library to the latest version: https://github.com/Adyen/adyen-java-api-library/releases
2. Upgrade your Adyen Dropin/Web Components to the latest version
3. This module is successful when you can ensure that all functionality still works as-is.

* Tip: Can we write tests *before doing the upgrade* to make sure that gift cards, donations, payments still work?
  * Make an iDeal payment automatically
  * Make a credit card payment automatically
  * Make a Klarna payment automatically
  * Make a donation
  * Make a gift card payment



This module is finished when everything still works after the upgrades.


## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.
