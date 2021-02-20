# challengeLib
This repository contains the challenge android library in the `challengelib` folder embedded in a demo application.

## How to test the demo App
Open the `build.gradle` file at the root of the project with Android Studio, make a `Gradle Sync` and compile the project on your device or on a simulator.

## How to embed the library
There is no Github repository for the library only, so you have to clone or download this project and copy the `challengelib` folder and paste it into your application folder.

Then edit your `settings.gradle` and add the following line to include the library:

    include ':challengelib'

Finally edit your application `build.gradle` file to add the library as a dependency:

    dependencies {
        implementation project(path: ':challengelib')
        ...
    }

## How to use the library

### Initialize the ConsentManager
In the `onCreate` method of your main `Application` class, run the following code to initialize the Manager with the Application Context:
```kotlin
class MainApplication: Application() {  
    override fun onCreate() {  
        super.onCreate()  
        ConsentManager.initialize(this)  
    }  
}
```

### Set up the Manager to check Consent
In the `onCreate` method of your  `Activity`, run the following code to check if we need to show a dialog to the user to accept or deny the consent.
We also check if the last consent status was successfully sent to remote server, if not we try to send it again.
```kotlin
class MainActivity : AppCompatActivity() {        
    override fun onCreate(savedInstanceState: Bundle?) {  
        super.onCreate(savedInstanceState)        
        ConsentManager.instance.setUp(this)  
    }
}
```

### Get or Set Consent manually
You can get the saved consent status by running:
```kotlin
val consent = ConsentManager.instance.getConsentStatus()
```

You can also set the consent status manually and get the result of the update by running:
```kotlin
ConsentManager.instance.setConsentStatus(ConsentStatus.ACCEPTED, {
    // onSuccess
    Toast.makeText(context, "Consent set to ACCEPTED.", Toast.LENGTH_SHORT).show()  
}, { error ->
    // onError
    Toast.makeText(context, "Fail to set consent to ACCEPTED: $error", Toast.LENGTH_SHORT).show()  
})
```
