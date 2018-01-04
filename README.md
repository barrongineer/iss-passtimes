# ISS Pass Times


This is a simple Android application that uses the devices location to query the [International Space Station Pass Times API](http://open-notify.org/Open-Notify-API/ISS-Pass-Times/) and display the times that the ISS will pass overhead.


Technologies Used:
* Kotlin - JVM programming language
* Dagger 2 - Dependency injection
* Retrofit 2 - HTTP client
* RxJava / RxAndroid - Observables for HTTP responses
* Anko Commons - Toasts and progress dialogs
* Anko Coroutines - Easily keep background tasks off of the UI thread