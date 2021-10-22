# Infinity Movies App

## Installation
Clone this repository and import into **Android Studio**
```bash
git clone git@github.com:subhasmiththapa2502/Ecommerce.git
```
Android Studio Version - Android Studio Arctic Fox | 2020.3.1 Patch 3 [Android Studio Download Link](https://developer.android.com/studio)


Infinity Movies App is an app where you can browse different categories of movies and place an Order. 
It uses demo data from TMDB. 
It has 
# Home
It has three categories
UpComing Movies, Now Playing and Latest Movies
Each Categories has a horizontal Endless Recyclerview. To View the vertical list click on See All > 



In Product Detail Page, It displays main product information, Including Product photos
, Price, Description etc. 


In Profile page, User Profile can toggle RTL, Enable notification, Enable
location, Manage Addresses


In Cart User can add and edit and delete items added from the products

User can also add addresses from google map by either searching on the map or by selecting using a Marker on map. 

DeepLinking. 

To test the deeplinks. Please use any links from the [The Movie Database Product Detail Page](https://www.themoviedb.org/movie/now-playing) 
This link will give an option to open via the app. After clicking Open with the apps it will open the detail page of the Product. 

# Libraries used 
1. GSON for JSON parsing [GSON](https://github.com/google/gson)
2. Glide for Image Showing and Caching [Glide](https://github.com/bumptech/glide)
3. Retrofit for API calls [Retrofit](https://square.github.io/retrofit/)

# Other Third Party 
1. Chip Navigation Bar for bottom tab[Chip Navigation](https://github.com/ismaeldivita/chip-navigation-bar)
2. ViewPager 2 for image carousel [ViewPager 2](https://developer.android.com/jetpack/androidx/releases/viewpager2)
3. Rounded ImageView [Rounded Image View](https://search.maven.org/artifact/com.makeramen/roundedimageview/2.3.0/jar)
