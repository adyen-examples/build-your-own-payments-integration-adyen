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

0. Enable donations in the Customer Area (& an ADP in the Backoffice).
1. Prepare your backend to handle an Adyen giving flow: https://docs.adyen.com/online-payments/donations/web-component/
    * You'll notice that you have to change your existing backend and frontend flows.
2. Prepare your backend to handle the incoming donation webhook.
    * In `Webhookresource.java`, build the logic to handle the `NotificationRequest` incoming, and print some useful information on the screen.
    * Validate the HMAC signature of the webhook.
    * _Tip: Notice that the webhook is different and that you'll have to create a different endpoint to receive this webhook._
3. Update your frontend so that it contains a donation screen that calls your backend when a donation-amount is specified.
    * Using Adyen Components, add and mount the `<div>` accordingly. The button should call your respective endpoint that you've implemented in step 1.
    * _Tip: You can save the donationToken/pspReference in your cookie session._
4. Perform a successful donation to finish this module and make sure to receive the webhook event.

