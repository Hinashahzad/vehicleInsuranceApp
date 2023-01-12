# Vehicle Insurance Company Mobile Interface Design
Mobile Application

## Achievements
### Features
1. Login
2. Logout
3. Modify the current password
4. Access claims
5. Insert new claims
6. Update claims
7. offline operations
8. Map of the accidents
9. Photo of the accident

## Baseline architecture
### Data structure Maintained by application
In the vehicle insurance application data structures that are mainly used during the implementation of the baseline architecture are Array, objects and files. Array data structure has been used to store the claims information. For instance, in the vehicle insurance application ClaimResponse is a class that contains a number of claims, claimsId, claimsDes, claimPhoto, claimLocation, claimStatus and each claims information is saved in an array data structure against each userId.Person.json and claim.json file is also a data structure that is mainly used to implement the baseline architecture of the application. Server is responsible to save the user data and claims data in the form of json data structure. Reading and writing through these json files makes possible to implement the vehicle insurance application baseline functionality. In addition, claimPhoto taken by the client is saved into the claimPhoto array in claim.json file and is also a data structure to make the implementation possible.Two objects person and claims has used to access the person information for instance, email id, password, user id etc and claim object contains all the information about the claims for instance claim description, status, location, claims id. 
### Login
Launching the mobile application it opens the LoginActivity. The user authenticated himself by providing the email address and password, that is encrypted to MD5 hash. User enters the same email and password that is stated in person.json file and after the verification they are successfully logged on the mobile application and then open the ClaimOverviewActivity. First time the client logs in it will be through the remote method PostRemoteLogin. Later it will first try to login based on the locally stored email and password.
### Logout
Logout will open the LoginActivity while clearing all tasks and creating a new history stack. On logout all claims and user data related to the user that logout is deleted from the device. The logout is a menu option on the claim overview page.

### Modify Password
In the options menu you have a change password item. When the user selects that menu item a dialog box will appear. To modify the password, the user must enter the correct current password, new password and repeat the new password. If input is correct it will update the SharedPreferences and invoke remote method /methodPostChangePasswd on the server.

### Access claims

The claims are downloaded from the server by the help of Volley, all the communication connected to claims is in a communication layer, ServerCommunicationInterface. The communication layer uses the userId passed from LoginActivity. When the claims are downloaded from the server they are parsed to a ClaimResponse object by the use of GSON. Then the ClaimResponse object is mapped to an array of Claim objects. This array is inserted into a custom Adapter named ClaimsListAdapter. ClaimsListAdapter creates a clickable list of claims that takes the user to the specific claim. The specific claim is displayed by passing the information as String to the ClaimDetailsActivity that displays the information in the ReadClaimFragment. When the claims overview page is resumed it will reload the claims.

### Insert new claims

The top button on the claim overview is an add claim button. The button takes the user to ClaimDetailsActivity and shows an empty AddEditClaimFragment. The fields can be filled in and when pressing the add button the information is passed to ClaimDetailsActivity by a listener. The claim is sent to the server by the use of the communication layer, then a ReadClaimFragment is opened to display the added claim. The client can not add more than five claims.

### Update claim

Update claim is opened by pressing the edit button on the read page of a claim. The button opens an AddEditClaimFragment that contains the data from the claim. Here the user can change or update the information of the claim, then press the save save button to pass the information to ClaimDetailsActivity by a listener. The claim is sent to the server by the use of the communication layer, then a ReadClaimFragment is opened to display the updated claim.

## Advances Features
### Map of the accident

The map is displayed in a MapFragment. The first way to use the map is as a read map. Here you can’t change the marker, it will just be the map with a set marker. This can be used by pressing the location button in the read page for a claim. The button is gray and dissabled if a location is not set.

In the edit page the user can also press the location button but here it will open a map with a draggable marker. If the user presses the select location button the latitude and longitude is passed back to the AddEditFragment through the ClaimDetailsActivity, where it will set the location on the claim. The map is implemented by the use of GoogleMap. The MapFragment is laid on top of the AddEditClaimFragment so the claim’s unsaved changes are not removed. It has two different button layouts that it will show or hide based on if it is an edit or read map.

### Photo of the accident

The photo is shown in the claim read page, if the photo is not in the described file location it will download the image file from the server to display it and save it locally. When selecting a picture the user will press either the add or change image button this will open the inbuilt action_pick intent and then get the picture from onActivityResult where it will first start to create the file and upload the picture to the server after the save or add button for the claim is pressed. If the upload of the picture failed or the client is not connected it will still store the file and mark it for need upload in SP. 

## Limitation

1. If status can be overridden by the client if the client updates the claim between when the claim was downloaded and updated from client side. This window could be big if the client is not connected to the server.
2. Does not delete image file when logout
3. If server connects between update and getClaim the update can be overwritten
4. If you change password on the server you can still login with the old password if it is stored on the device, it will even overwrite the password if the password has been changed on the device.
