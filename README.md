# Build your own Payment Integration with Adyen

## Introduction

This repository is a step-by-step guide to building your own payment integration with Adyen. We will start from a simple drop-in integration, and build our way up to a fully customized checkout page.

Each module starts at the relevant tag number. For example, to start module 2, checkout tag `module-2-start`.
In case you want to skip a module or compare your solution, you can checkout the corresponding tag. For example, `module-2-end` for module 2.

_Note that the workshop is intended to highlight some of the pains of our customers when building and upgrading. We know there might be better ways to do things, with a deeper knowledge of the platform._

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
## Starting the workshop

Pick your module to start working :

* [Module 0 : Building a simple checkout page using sessions](MODULE_0.md)


## Contacting us

If you have any questions, feel free to contact us at devrel@adyen.com.

* [Julien Lengrand-Lambert](https://github.com/jlengrand)
* [Kwok He Chu](https://github.com/Kwok-he-Chu)
