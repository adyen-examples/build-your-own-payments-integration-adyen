# Module 3 : Adding donations!

* You can keep using your own code to implement this module.
* A clean starting template is available at all times with the `module-3-start` tag.
    * `git checkout module-3-start` to get the starting template.
* A proposed solution is available with the `module-3-end` tag.
    * `git checkout module-3-end` to get the proposed solution.

## Briefing

The company wants to give their shoppers the option to donate, after every successful payment, as part of their ongoing charity efforts.
It's actually nice, because the latest version of the library supports donations out of the box! We're super glad we upgraded. 
We also see [in the docs](https://docs.adyen.com/online-payments/donations/web-component/) that donations are only supported in the advanced flow integration. We're glad we moved back from sessions a few hours ago. 

Let's implement this!

## Your job :

_Note: For this module, the facilitator will need an ADP in the backoffice. If you have enabled donations in the Customer Area but you don't get any donationToken back in your payment response, check with them._

0. [Enable donations](https://docs.adyen.com/online-payments/donations/testing/#enable-giving-ca) in the Customer Area.
1. Prepare your backend to handle an [Adyen giving flow](https://docs.adyen.com/online-payments/donations/web-component/)
    * In `CheckoutResource.java`, make sure to set the donationToken as part of the payment request in the `payments` method.
    * Don't forget to use the donationService to store the donation token for future use.
    * In `DonationResource.java`, implement the `donations` method that will handle the incoming donation request.
2. Prepare your backend to handle the incoming donation webhook.
    * In `Webhookresource.java`, build the logic to handle the `NotificationRequest` incoming, and print some useful information on the screen.
    * Validate the HMAC signature of the webhook.
    * _Tip: Notice that the webhook is different and that you'll have to create a different endpoint to receive this webhook._
3. Update your frontend so that it contains a donation screen that calls your backend when a donation-amount is specified.
    * In `result.html` , we had a new state, "donated", which will appear after someone has paid, and donated!
    * In `result.html`, add the donation container, which should only show when a payment has been procesed (hint: , you can use the following thymeleaf logic : `th:if="${type == 'success' || type == 'pending' }`).
    * In the same file, don't forget to import the necessary javascript file to handle the donation logic.
4. In `adyenGivingImplementation.js`, implement the donations logic and the proper configuration objects.
4. Perform a successful donation to finish this module and make sure to receive the webhook event. Note that not all payment methodscan be used for donations. 

