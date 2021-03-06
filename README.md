# CosmeticsScan
## About
Android application created as project for WPAM subject in WUT. Goal of the application is to read ingredients of the cosmetic from the photo and display their function. Function of the cosmetic is taken from `cosmetic ingredient database` which is accesible through API. Also, option to save favourite cosmetics was implemented in a backend databse. 

## Implementation 
* image of the ingredients list is read from the gallery 
* text is procesed with OCR 
* ingredients are send through API to database and information about them is displayed
* user can save cosmetic in backend databse, which can be later viewed or deleted 

## Technology 
* Application was created in `Kotlin` with utilization of `RxJava` and `Retrofit` to make API calls. 
* On device `Firebase ML Vision` was used for OCR. 
* Backend database of favorite cosmetics was created in `Python Django framework` and it is accesible by API. 
* Used database of the ingredients and their function is https://public.opendatasoft.com/explore/dataset/cosmetic-ingredient-database-ingredients-and-fragrance-inventory/api/


