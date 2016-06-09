# My T-Mobile watch #

Simple watch application for the My T-Mobile Customer API(CAPI). See https://capi.t-mobile.nl for the documentation on the API.

The watch displays postpaid bundle data for customers with subscriptions. To use the app you need to log in with the handheld app first.

### What is this repository for? ###

This repository is provided as an example of a simple app using the T-Mobile CAPI.

### How do I get set up? ###

To use the CAPI you need to atleast have access to the CAPI.

* Make sure you have jdk 8 for retrolambda
* Add your client_id/key to oauth_clientkey in mobile/main/res/values/api.xml
* Add your app_scheme in mobile/main/res/values/api.xml

### 3rd party libraries used ###

* [ReactiveX](http://reactivex.io/)
Provides a simple interface for working with data. This also enable some complex operations on different network calls with the CAPI and the Wearable datalayer. 

* [Retrofit 2](http://square.github.io/retrofit/)
"A type-safe HTTP client for Android and Java". To simplify JSON parsing and network calls. This is used in combination with ReactiveX and GSON.

* [Retrolambda](https://github.com/orfjackal/retrolambda)
To enable java 8 method references and lambda expressions. This makes code more readable especially in combination with ReactiveX. However this is not used on purpose in the datalayer module to enable the datalayer module to be less dependent in other applications if used.

### Modules ###

The app is divided into 3 different modules: 

* mobile(handheld device)
* wear(wearable device)
* datalayer(module for wearable communication)

### Differences in Util and Helper classes ###
A helper class is only used in the app context while a utility class could be used in any app.