const clientKey = document.getElementById("clientKey").innerHTML;

// Used to finalize a checkout call in case of redirect
const type = document.getElementById("type").innerHTML;

var remainingAmountToPay = document.getElementById("totalAmount").innerHTML;

var checkout;

// Sends a POST request
async function sendPostRequest(url, data) {
    const res = await fetch(url, {
        method: "POST",
        body: data ? JSON.stringify(data) : "",
        headers: {
            "Content-Type": "application/json",
        },
    });

    return await res.json();
}

// Gift card configuration
const giftCardConfiguration =  {
    onBalanceCheck: async function (resolve, reject, data) {
        console.info(data);
        console.log('onBalanceCheck');

        const balanceCheckResponse = await sendPostRequest("/api/balanceCheck", data);
        console.info(balanceCheckResponse);

        resolve(balanceCheckResponse);
    },
    onOrderRequest: async function (resolve, reject, data) {
        console.info(data);
        console.log('onOrderRequest');

        const createOrderResponse = await sendPostRequest("/api/createOrder");
        console.info(createOrderResponse);

        resolve(createOrderResponse);
    },
    onOrderCancel: async function (order) {
        console.info(order);
        console.log('onOrderCancel');

        const orderCancelResponse = await sendPostRequest("/api/cancelOrder", order);
        console.info(orderCancelResponse);

        checkout.update({
            paymentMethodsResponse: await sendPostRequest("/api/getPaymentMethods"),
            order: null,
            amount: { currency: "EUR", value: 0 }
        });
    }
};

// Create Adyen Checkout configuration
async function createAdyenCheckout(paymentMethodsResponse) {
    return new AdyenCheckout({
        amount: {
            currency: "EUR",
            value: remainingAmountToPay,
        },
        paymentMethodsResponse: paymentMethodsResponse,
        clientKey: clientKey,
        locale: "en_US",
        countryCode: "NL",
        environment: "test",
        showPayButton: true,
        paymentMethodsConfiguration: {
            ideal: {
                showImage: true
            },
            card: {
                hasHolderName: true,
                holderNameRequired: true,
                name: "Credit or debit card"
            },
            // You can specify a custom logo for a gift card brand when creating a configuration object
            // See https://docs.adyen.com/payment-methods/gift-cards/web-drop-in#optional-customize-logos
        },
        onSubmit: async (state, component) => {
            // Adyen provides a "Pay button", to use the Pay button for each payment method, set `showPayButton` to true
            // The 'Pay button'' triggers this onSubmit() event
            // If you want to use your own button and then trigger the submit flow on your own
            // Set `showPayButton` to false and call the .submit() method from your own button implementation, for example: component.submit()
            console.log("onSubmit");
            if (!state.isValid) {
                throw new Error("State is not valid");
            }

            var response = await sendPostRequest("/api/initiatePayment", state.data);
            console.info(response);

            if (response.action) {
                component.handleAction(response.action);
            } else if (response.order && response.order?.remainingAmount?.value > 0) { // Handles partial orders for you and updates the UI
                const order = {
                    orderData: response.order.orderData,
                    pspReference: response.order.pspReference
                };

                const subtractedGiftCardBalance = remainingAmountToPay - response.order.remainingAmount?.value;

                // Show remaining amount on the drop-in
                remainingAmountToPay = response.order.remainingAmount?.value;

                // Show remaining amount
                const remainingAmountElement = document.getElementById('remaining-due-amount');
                remainingAmountElement.textContent = (remainingAmountToPay / 100).toFixed(2);

                // Show the subtracted balance of the gift card to the shopper if there are any changes
                showGiftcardAppliedMessage(subtractedGiftCardBalance);

                // Show all payment method buttons
                showAllPaymentMethodButtons();

                // Hide the mounted components
                document.getElementById("giftcard-container-item").hidden = true;
                document.getElementById("ideal-container-item").hidden = true;
                document.getElementById("scheme-container-item").hidden = true;

                // TODO: update checkout
                checkout.update({
                    paymentMethodsResponse: paymentMethodsResponse,
                    order,
                    amount: response.order.remainingAmount
                });
            } else {
                switch (response.resultCode) {
                    case "Authorised":
                        window.location.href = "/result/success";
                        break;
                    case "Pending":
                    case "Received":
                        window.location.href = "/result/pending";
                        break;
                    case "Refused":
                        window.location.href = "/result/failed";
                        break;
                    default:
                        window.location.href = "/result/error";
                        break;
                }
            }
        },
        onAdditionalDetails: async (state, component) => {
            await handleSubmission(state, component, "/api/submitAdditionalDetails");
        },
        onPaymentCompleted: (result, component) => {
            console.info("onPaymentCompleted");
            console.info(result, component);
            handleServerResponse(result, component);
        },
        onError: (error, component) => {
            console.error("onError");
            console.error(error.name, error.message, error.stack, component);
            handleServerResponse(error, component);
        },
    });
}

// Start gift card checkout experience
async function startCheckout() {
    console.info('Start checkout...');
    try {
        const paymentMethodsResponse = await sendPostRequest("/api/getPaymentMethods");

        try {
            if (checkout == null) {
                checkout = await createAdyenCheckout(paymentMethodsResponse);
            }
        } catch(exception) {
            console.warn("Could not mount the gift card component.")
        }

        // Gift card component
        const giftCardComponent = checkout.create('giftcard', giftCardConfiguration).mount(document.getElementById("giftcard-container-item"));
        const giftCardButton = document.querySelector('.giftcard-button-selector')

        // iDeal component
        const idealComponent = checkout.create('ideal', giftCardConfiguration).mount(document.getElementById("ideal-container-item"));
        const idealButton = document.querySelector('.ideal-button-selector');

        // Scheme component
        const schemeComponent = checkout.create('scheme', giftCardConfiguration).mount(document.getElementById("scheme-container-item"));
        const schemeButton = document.querySelector('.scheme-button-selector');

        /// Bind all buttons
        giftCardButton.addEventListener('click', () => {
            if (remainingAmountToPay == 0) {
                alert("Enter some items in your shopping cart");
                return;
            }

            // No longer allow the shopper to change the items.
            document.getElementById("add-headphones-button").hidden = true;
            document.getElementById("add-sunglasses-button").hidden = true;

            hideAllPaymentMethodButtons();
            document.getElementById("giftcard-container-item").hidden = false;
            document.getElementById("ideal-container-item").hidden = true;
            document.getElementById("scheme-container-item").hidden = true;
        });

        schemeButton.addEventListener('click', () => {
            hideAllPaymentMethodButtons();
            document.getElementById("giftcard-container-item").hidden = true;
            document.getElementById("ideal-container-item").hidden = true;
            document.getElementById("scheme-container-item").hidden = false;
        });

        idealButton.addEventListener('click', () => {
            hideAllPaymentMethodButtons();
            document.getElementById("giftcard-container-item").hidden = true;
            document.getElementById("ideal-container-item").hidden = false;
            document.getElementById("scheme-container-item").hidden = true;
        });

    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details");
    }
}

// Show all payment method buttons
function showAllPaymentMethodButtons() {
    const buttons = document.getElementsByClassName('payment-method-selector-button');
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].hidden = false;
    }
}

// Hides all payment method buttons
function hideAllPaymentMethodButtons() {
    const buttons = document.getElementsByClassName('payment-method-selector-button');
    for (let i = 0; i < buttons.length; i++) {
        buttons[i].hidden = true;
    }
}

// Appends a visual cue when a gift card has been successfully applied
// Pass parameter which states how much of the gift card amount is spent
function showGiftcardAppliedMessage(giftcardSubtractedBalance) {
    let overviewList = document.querySelector('.order-overview-list');

    // Add <li>
    let liElement = document.createElement('li');
    liElement.classList.add('order-overview-list-item');

    // Add <p>
    let pElement = document.createElement('p');
    pElement.classList.add('order-overview-list-item-giftcard-balance');

    // Show 'Gift card applied -50.00' (example)
    pElement.textContent = 'Gift card applied -' + (giftcardSubtractedBalance / 100).toFixed(2);

    // Append the child element to the list
    liElement.appendChild(pElement);
    overviewList.appendChild(liElement);
}

// Event handlers called when the shopper selects the pay button,
// or when additional information is required to complete the payment
async function handleSubmission(state, component, url) {
    try {
        const res = await sendPostRequest(url, state.data);
        handleServerResponse(res, component);
    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details");
    }
}

// Handle server response
function handleServerResponse(res, _component) {
    console.info(res);
    switch (res.resultCode) {
        case "Authorised":
            window.location.href = "/result/success";
            break;
        case "Pending":
        case "Received":
            window.location.href = "/result/pending";
            break;
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            window.location.href = "/result/error";
            break;
    }
}

startCheckout();