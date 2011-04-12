Parcelgen-Example
=================

An application which shows the use of Parcelgen in a simple Android app.

This application makes requests to the public [Yelp API][yelpapi], constructs java objects from the JSON response, and passes the constructed objects to two different sub-activities. It uses objects constructed with Parcelgen to parse the JSON server response, and parcelgen handles passing those objects between activities within the app.

How to Use
----------

Rename `res/values/api_keys.xml.default` to `res/values/api_keys.xml`, uncomment the XML in the file, and replace its keys with your own [Yelp API V2.0 keys][yelpapiaccess]. Import the project into Eclipse, and run!

A Note on Yelp API Use
----------------------

This example's Yelp API code uses on the [Yelp API Java Example][yelpexample].  The example uses [Scribe][scribe] to make OAuth singned requests to Yelp's API. This is sample code and is not intended for production use. It does not follow best practices when making web requests from Android applications. After writing this example I cannot recommend Scribe for use in Android applications using OAuth; I recommend [Signpost][signpost] instead. 

[yelpapi]: http://www.yelp.com/developers/documentation/v2/search_api
[yelpexample]: https://github.com/Yelp/yelp-api/tree/master/v2/java
[scribe]: https://github.com/fernandezpablo85/scribe-java
[signpost]: https://github.com/kaeppler/signpost
[yelpapiaccess]: http://www.yelp.com/developers/getting_started/api_access