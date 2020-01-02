from rest_framework import serializers
from .models import Cosmetic
from .models import Ingredient


class IngredientSerializer(serializers.ModelSerializer):
    # cosmetics = serializers.PrimaryKeyRelatedField(many=True, queryset=Cosmetic.objects.all())

    class Meta:
        model = Ingredient
        fields = ('id', 'name', 'function', 'favourite')


class CosmeticSerializer(serializers.ModelSerializer):
    ingredients_list = IngredientSerializer(many=True, read_only=True)

    class Meta:
        model = Cosmetic
        fields = ('id', 'name', 'favourite', 'ingredients', 'ingredients_list')

