from django.shortcuts import render
from rest_framework import viewsets
from .serializers import CosmeticSerializer, IngredientSerializer
from .models import Cosmetic, Ingredient
# Create your views here.


class CosmeticView(viewsets.ModelViewSet):
    queryset = Cosmetic.objects.all().order_by('name')
    serializer_class = CosmeticSerializer


class IngredientView(viewsets.ModelViewSet):
    queryset = Ingredient.objects.all().order_by('name')
    serializer_class = IngredientSerializer
