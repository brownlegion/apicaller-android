# ApiCaller for Android

Create basic API requests using this library for your Android application. Add the module into your application, then setup your calls accordingly.

Example request:

```java
private void makeApiRequest() throws Exception {

 //Build your JSON parameters
 JSONObject parameters = new JSONObject();
 parameters.put("String1", "value1");
 parameters.put("String2", "value2");
 int five = 5;
 parameters.put("integer", five);
 
 //Add whatever you want to it
 JSONObject nestedParameters = new JSONObject();
 nestedParameters.put("booleanValue", true);
 parameters.put("nestedParameters", nestedParameters);
 
 //Even JSONArrays!
 ArrayList<String> array = new ArrayList<>();
 array.add("hello");
 array.add("my");
 array.add("name");
 array.add("is");
 array.add("what");
 parameters.put("arrayOfStrings", new JSONArray(array));
 
 //Optionally add your own custom headers
 HashMap<String, String> headers = new HashMap<>();
 headers.put("Content-Type", "application/json");
 headers.put("Accept", "application/json");
 
 String myEndpoint = "https://my.secure.web.server.com/my/endpoint";
 boolean isMyEndpointSecure = myEndpoint.startsWith("https");
 
 ApiCaller.getInstance().sendPost(myEndpoint, parameters, headers, 10000, isMyEndpointSecure, responseHandler); //Send the request with headers and a custom timeout

 ApiCaller.getInstance().sendPost(myEndpoint, parameters, headers, isMyEndpointSecure, responseHandler); //Send the request with headers but no custom timeout

 ApiCaller.getInstance().sendPost(myEndpoint, parameters, isMyEndpointSecure, responseHandler); //Send the request without any headers or custom timeout

 ApiCaller.getInstance().sendGet(myEndpoint, headers, 10000, isMyEndpointSecure, responseHandler); //Send a GET request with your headers and timeout

 ApiCaller.getInstance().sendPostArray(myEndpoint, new JSONArray(array), isMyEndpointSecure, responseHandler); //Send a POST request using only a JSON array with no headers or custom timeout
}

private ApiCaller.ResponseCallback responseHandler = new ApiCaller.ResponseCallback() {

 @Override
 public void onResponse(String response) {
 	//Handle the response here
 }

 @Override
 public void onError(String error) {
 	//Handle the error here
 }
};
```

Some notes:
* Current default timeout is set to 10 seconds
* Feel free to manipulate the module in any way you see fit
* [Line 25 of the ApiCaller class](https://github.com/brownlegion/apicaller-android/blob/master/tools/src/main/java/com/kdawg/apicaller/ApiCaller.java#L25) is where you would set the flag to send your requests to a place with an untrusted certificate or not. CHANGE ACCORDINGLY