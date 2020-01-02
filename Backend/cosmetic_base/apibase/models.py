from django.db import models


class Ingredient(models.Model):
    name = models.CharField(max_length=50)
    function = models.CharField(max_length=300)
    favourite = models.BooleanField(default=False)

    def __str__(self):
        return self.name+' is: '+self.function


class Cosmetic(models.Model):
    name = models.CharField(max_length=50)
    favourite = models.BooleanField(default=False)
    ingredients = models.ManyToManyField(Ingredient)

    def __str__(self):
        return self.name
