# RunTracker
A Run Tracker app build on Kotlin.

A Run Tracker Application build on Android Studio using Kotlin as a Primary Language.  
App is build using MVVM architecture  

Project Description and Guide.  
User can add his Name and Weight in the first step to start using the app.  
Navigation components is been used in this app to minimize the use of activities and instead used Fragments inplace.  
In the first tab "Your Runs"user will see his/her runs which he can sort according to preference. Sorting can be done on Date, RunningTime,Avg Speed, DIstance and Calories Burned.  
User can click on Floationg Action Button on bottom right corner to start a new Run.  
In the second tab "Statistics" user can view his/her all over stats(respect to all runs) and also a barChart is displayed for all runs.  
In the third tab "Settings" user can edit his/her name and weight and click on Apply Changes button to save changes.  

Highlighted Features:-
This App shows a Foreground Notification whenever a run is started. The notification displays the timer and also a button to pause or resume run.  
Dependencies are injected using DAGGER-HILT.  
Also unit testcases are written for database Testing.  


