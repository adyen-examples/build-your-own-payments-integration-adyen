# Module 2 : Upgrading to the latest version of the library and Adyen Drop-in

* You can keep using your own code to implement this module.
* A clean starting template is available at all times with the `module-2-start` tag.
    * `git checkout module-2-start` to get the starting template.
* A proposed solution is available with the `module-2-end` tag.
    * `git checkout module-2-end` to get the proposed solution.

## Briefing

Project MyStore has been very successful! Your shop is running at full speed and you're selling more and more headphones and sunglasses every day.
We want to keep up to date with the newer version of the libraries and drop-in, so we can benefit from the latest features and bug fixes.

This should only take us a few minutes! 

## Your job :

1. Upgrade your Adyen Drop-in/Web Components to the latest version: See [Drop-In releases](https://docs.adyen.com/online-payments/release-notes/?integration_type=web)
    * In `layout.html`, update the `adyen-web` version to the latest version.
    * Fix all the potential compilation errors in `adyenImplementation.js`.
2. Upgrade your Java library to the latest version: See [Java releases](https://github.com/Adyen/adyen-java-api-library/releases)
    * In `build.gradle`, update `adyen-java-api-library` to the latest version.
    * Fix all the compilation errors in `CheckoutResource.java` and `WebhookResource.java`.
    * _Note:_ After bumping up the version and building the project, we've seen some cases of where your IntelliJ is unable to pick-up the new model changes. You may need to clear your cache and restart your IDE to fix this.
3. This module is successful when the website works just as before. Test the different payments methods to make sure everything is working as expected.
