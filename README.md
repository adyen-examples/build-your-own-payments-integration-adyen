# Build your own Payment Integration with Adyen

## Introduction

This repository is a step-by-step guide to building your own payment integration with Adyen. We will start from a simple drop-in integration, and build our way up to a fully customized checkout page.

Each module starts at the relevant tag number. For example, to start module 2, checkout tag `module-2-start`.
In case you want to skip a module or compare your solution, you can checkout the corresponding tag. For example, `module-2-end` for module 2.

_Note that the workshop is intended to highlight some of the pains of our customers when building and upgrading. We know there might be better ways to do things, with a deeper knowledge of the platform._

The list of modules is as such : 

* [Module 0 : Building a simple checkout page using sessions](https://github.com/adyen-examples/build-your-own-payments-integration-adyen#module-0--building-a-simple-checkout-page)

### Context of the code repository.

In this workshop, we are using Java and Spring Boot, together with a static frontend based on thymelead template.
We use those because we want to reduce the amount of prerequisite knowledge (like a frontend framework) and use a strongly typed language for the backend to build empathy for the customer experience.

_In case the static frontend environment is not to your liking, feel free to implement your own frontend solution using the framework of your choice. However, note that it will take precious time away from the actual exercises._

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

## Module 0 : Building a simple checkout page

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

## Module 1 : Upgrading to the latest version of the library and web components.

* To be continued!


## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.

* [Julien Lengrand-Lambert](https://github.com/jlengrand)
* [Kwok He Chu](https://github.com/Kwok-he-Chu)
