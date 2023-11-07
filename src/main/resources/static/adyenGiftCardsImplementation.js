const clientKey = document.getElementById("clientKey").innerHTML;

// Used to finalize a checkout call in case of redirect
const type = document.getElementById("type").innerHTML;

var remainingAmountToPay = document.getElementById("totalAmount").innerHTML;

var checkout;

// Gift card configuration
const giftCardConfiguration =  {
    onBalanceCheck: async function (resolve, reject, data){
        console.log('onBalanceCheck');

        const balanceCheckResponse = await sendPostRequest("/api/balanceCheck", data);
        console.info(balanceCheckResponse);
        console.info(balanceCheckResponse.balance.value);

        if (balanceCheckResponse.resultCode == "Success")  {
            const response = await sendPostRequest("api/initiatePayment", data);
            handleServerResponse(response);
        }
        else if (balanceCheckResponse.resultCode == "NotEnoughBalance") {
            await this.onOrderRequest(resolve, reject, balanceCheckResponse);
        }
        else {
            throw new Error("error handling balanceCheckResponse.");
        }
    },
    onOrderRequest: async function (resolve, reject, data) {
        console.log('onOrderRequest');
        const createOrderResponse = await sendPostRequest("/api/createOrder", data);
        console.info(createOrderResponse);
        await handleOnOrderCreated(createOrderResponse);
    },
    onOrderCancel: async function (order) {
        console.log('onOrderCancel');

        const orderCancelResponse = await sendPostRequest("/api/cancelOrder");
        console.info(orderCancelResponse);
        checkout.update(order);
    }
};

// Create Adyen Checkout configuration
async function createAdyenCheckout(paymentMethodsResponse) {
    return new AdyenCheckout({
        paymentMethodsResponse: paymentMethodsResponse,
        clientKey: clientKey,
        locale: "en_US",
        environment: "test",
        showPayButton: true,
        paymentMethodsConfiguration: {
            ideal: {
                showImage: true,
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
            if (state.isValid) {
                console.log(state.data);

                await handleSubmission(state, component, "/api/initiatePayment");
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

var giftCardComponent;

// Start gift card checkout experience
async function startCheckout() {
    console.info('Start checkout...');
    try {
        const paymentMethodsResponse = await sendPostRequest("/api/getPaymentMethods");

        try {
            if (checkout == null) {
                checkout = await createAdyenCheckout(paymentMethodsResponse);
            }
            giftCardComponent = checkout.create(type, giftCardConfiguration);
            giftCardComponent.mount(document.getElementById("giftcard-container"));
        } catch(exception) {
            console.warn("Could not mount the gift card component.")
        }

        // Mount your supported payment method components (e.g. 'ideal', 'scheme' etc)
        mountPaymentMethodButton('ideal');
        mountPaymentMethodButton('scheme');

        hideAllPaymentMethodButtons();

        // Mount gift card component
        mountGiftcardComponentButton();

        // Show the gift card button
        document.getElementById("add-giftcard-button").hidden = false;

    } catch (error) {
        console.error(error);
        alert("Error occurred. Look at console for details");
    }
}

// Add event listener that mounts gift card component when clicked
function mountGiftcardComponentButton() {
    // Adds gift card container and the eventlistener
    document.getElementById("add-giftcard-button")
        .addEventListener('click', async () => {
            if (remainingAmountToPay == 0) {
                console.warn('No items in cart');
                alert('No items in cart. Please add some headphones/sunglasses.');
                return;
            }

            try {
                giftCardComponent.mount(document.getElementById("giftcard-container"));
            } catch(exception) {
                console.warn("Could not mount the gift card component.")
            }

            // Hides all payment method buttons
            hideAllPaymentMethodButtons();

            // Show gift card component
            document.getElementById("giftcard-container").hidden = false;

            // Hide gift card button
            document.getElementById("add-giftcard-button").hidden = true;
        });
}

// Add event listener to the buttons and mount the respective component for the specified paymentMethodType when clicked
function mountPaymentMethodButton(paymentMethodType) {
    // Find <button> for the respective payment method
    let buttonElement = document.querySelector('.' + paymentMethodType + '-button-selector');

    // Add event listener to <button>
    buttonElement.addEventListener('click', async () => {
        const className = '.' + paymentMethodType + '-container-item';
        try {
            const paymentMethodComponent = checkout.create(paymentMethodType, giftCardConfiguration);
            paymentMethodComponent.mount(className);

        } catch (error) {
            console.warn('Unable to mount: "' + paymentMethodType + '" to the `<div class={paymentMethodType}-container-item></div>`.');
        }
    });
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

// Shows an error message when gift card is invalid
function showGiftCardErrorMessage(errorMessage) {
    let giftcardErrorMessageComponent = document.querySelector('#giftcard-error-message');
    // Show the error message
    giftcardErrorMessageComponent.textContent = errorMessage;
}

// Clears any (previous) error messages
function clearGiftCardErrorMessages() {
    let giftcardErrorMessageComponent = document.querySelector('#giftcard-error-message');
    giftcardErrorMessageComponent.textContent = '';
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

// Called when onOrderCreated is fired
async function handleOnOrderCreated(orderStatus) {
    console.log('handleOnOrderCreated');
    console.info(checkout);

    const response = await sendPostRequest("api/initiatePayment", giftCardComponent.state);

    switch (response.resultCode) {
        case "Authorised":
        case "Pending":
        case "Received":
            let subtractedGiftcardBalance = remainingAmountToPay - orderStatus.remainingAmount.value;
            remainingAmountToPay = orderStatus.remainingAmount.value;

            const remainingAmountElement = document.getElementById('remaining-due-amount');
            remainingAmountElement.textContent = (remainingAmountToPay / 100).toFixed(2);

            // Show the subtracted balance of the gift card to the shopper if there are any changes

            if (remainingAmountToPay > 0) {
                // Clears any (previous) error messages
                clearGiftCardErrorMessages();

                // Show 'Gift card applied' message
                showGiftcardAppliedMessage(subtractedGiftcardBalance);

                // Show payment method buttons
                showAllPaymentMethodButtons();

                // Hide gift card component
                giftCardComponent.unmount();
                document.getElementById("add-giftcard-button").hidden = true;
                document.getElementById("giftcard-container").hidden = true;
            }
            else
            {
                window.location.href = "/result/error";
            }
            break;
        case "Refused":
            window.location.href = "/result/failed";
            break;
        default:
            // Show an error message
            showGiftCardErrorMessage('Invalid gift card');
            break;
    }
}


// Calls your server endpoints
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