from django.contrib import admin

# Register your models here.
from .models import Cosmetic
from .models import Ingredient
admin.site.register(Cosmetic)
admin.site.register(Ingredient)
