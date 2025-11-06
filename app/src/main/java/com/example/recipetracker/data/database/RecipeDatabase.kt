package com.example.recipetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.recipetracker.data.dao.CookingHistoryDao
import com.example.recipetracker.data.dao.RecipeDao
import com.example.recipetracker.data.model.CookingHistory
import com.example.recipetracker.data.model.Recipe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Recipe::class, CookingHistory::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun cookingHistoryDao(): CookingHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getDatabase(context: Context): RecipeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RecipeDatabase::class.java,
                    "recipe_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateSampleData(database.recipeDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateSampleData(recipeDao: RecipeDao) {
            // Sample community recipes
            val sampleRecipes = listOf(
                Recipe(
                    name = "Classic Spaghetti Carbonara",
                    description = "A traditional Italian pasta dish with eggs, cheese, and bacon",
                    ingredients = """[
                        {"name":"Spaghetti","quantity":"400","unit":"g"},
                        {"name":"Eggs","quantity":"4","unit":""},
                        {"name":"Parmesan cheese","quantity":"100","unit":"g"},
                        {"name":"Bacon","quantity":"200","unit":"g"},
                        {"name":"Black pepper","quantity":"1","unit":"tsp"}
                    ]""",
                    steps = """[
                        {"stepNumber":1,"instruction":"Cook spaghetti in salted boiling water","duration":10},
                        {"stepNumber":2,"instruction":"Fry bacon until crispy","duration":5},
                        {"stepNumber":3,"instruction":"Mix eggs and parmesan","duration":2},
                        {"stepNumber":4,"instruction":"Combine all ingredients","duration":3}
                    ]""",
                    prepTime = 10,
                    cookTime = 20,
                    servings = 4,
                    difficulty = "Medium",
                    cuisine = "Italian",
                    isVegetarian = false,
                    isNutFree = true,
                    isDairyFree = false,
                    isPersonal = false
                ),
                Recipe(
                    name = "Vegan Buddha Bowl",
                    description = "A nutritious bowl packed with vegetables, grains, and plant-based protein",
                    ingredients = """[
                        {"name":"Quinoa","quantity":"1","unit":"cup"},
                        {"name":"Chickpeas","quantity":"400","unit":"g"},
                        {"name":"Sweet potato","quantity":"1","unit":""},
                        {"name":"Kale","quantity":"2","unit":"cups"},
                        {"name":"Avocado","quantity":"1","unit":""},
                        {"name":"Tahini","quantity":"3","unit":"tbsp"}
                    ]""",
                    steps = """[
                        {"stepNumber":1,"instruction":"Cook quinoa according to package","duration":15},
                        {"stepNumber":2,"instruction":"Roast sweet potato and chickpeas","duration":25},
                        {"stepNumber":3,"instruction":"Massage kale with olive oil","duration":2},
                        {"stepNumber":4,"instruction":"Assemble bowl and drizzle tahini","duration":3}
                    ]""",
                    prepTime = 15,
                    cookTime = 30,
                    servings = 2,
                    difficulty = "Easy",
                    cuisine = "Mediterranean",
                    isVegan = true,
                    isVegetarian = true,
                    isGlutenFree = true,
                    isNutFree = false,
                    isDairyFree = true,
                    isPersonal = false
                ),
                Recipe(
                    name = "Gluten-Free Chocolate Chip Cookies",
                    description = "Delicious cookies that everyone can enjoy",
                    ingredients = """[
                        {"name":"Gluten-free flour","quantity":"2","unit":"cups"},
                        {"name":"Butter","quantity":"1","unit":"cup"},
                        {"name":"Sugar","quantity":"1","unit":"cup"},
                        {"name":"Eggs","quantity":"2","unit":""},
                        {"name":"Chocolate chips","quantity":"2","unit":"cups"},
                        {"name":"Vanilla extract","quantity":"1","unit":"tsp"}
                    ]""",
                    steps = """[
                        {"stepNumber":1,"instruction":"Cream butter and sugar","duration":5},
                        {"stepNumber":2,"instruction":"Add eggs and vanilla","duration":2},
                        {"stepNumber":3,"instruction":"Mix in flour","duration":3},
                        {"stepNumber":4,"instruction":"Fold in chocolate chips","duration":2},
                        {"stepNumber":5,"instruction":"Bake at 350°F for 12 minutes","duration":12}
                    ]""",
                    prepTime = 15,
                    cookTime = 12,
                    servings = 24,
                    difficulty = "Easy",
                    cuisine = "American",
                    isGlutenFree = true,
                    isVegetarian = true,
                    isNutFree = true,
                    isDairyFree = false,
                    isPersonal = false
                ),
                Recipe(
                    name = "Thai Green Curry",
                    description = "Aromatic and spicy Thai curry with vegetables",
                    ingredients = """[
                        {"name":"Green curry paste","quantity":"3","unit":"tbsp"},
                        {"name":"Coconut milk","quantity":"400","unit":"ml"},
                        {"name":"Chicken breast","quantity":"500","unit":"g"},
                        {"name":"Thai basil","quantity":"1","unit":"cup"},
                        {"name":"Bell peppers","quantity":"2","unit":""},
                        {"name":"Fish sauce","quantity":"2","unit":"tbsp"}
                    ]""",
                    steps = """[
                        {"stepNumber":1,"instruction":"Fry curry paste in oil","duration":2},
                        {"stepNumber":2,"instruction":"Add coconut milk","duration":2},
                        {"stepNumber":3,"instruction":"Add chicken and cook through","duration":10},
                        {"stepNumber":4,"instruction":"Add vegetables and simmer","duration":8},
                        {"stepNumber":5,"instruction":"Finish with basil","duration":1}
                    ]""",
                    prepTime = 15,
                    cookTime = 25,
                    servings = 4,
                    difficulty = "Medium",
                    cuisine = "Thai",
                    isGlutenFree = true,
                    isDairyFree = true,
                    isNutFree = false,
                    isPersonal = false
                ),
                Recipe(
                    name = "Classic Margherita Pizza",
                    description = "Simple and delicious traditional Italian pizza",
                    ingredients = """[
                        {"name":"Pizza dough","quantity":"1","unit":"ball"},
                        {"name":"Tomato sauce","quantity":"1","unit":"cup"},
                        {"name":"Fresh mozzarella","quantity":"250","unit":"g"},
                        {"name":"Fresh basil","quantity":"1","unit":"bunch"},
                        {"name":"Olive oil","quantity":"2","unit":"tbsp"}
                    ]""",
                    steps = """[
                        {"stepNumber":1,"instruction":"Roll out pizza dough","duration":5},
                        {"stepNumber":2,"instruction":"Spread tomato sauce","duration":2},
                        {"stepNumber":3,"instruction":"Add mozzarella","duration":2},
                        {"stepNumber":4,"instruction":"Bake at 450°F","duration":12},
                        {"stepNumber":5,"instruction":"Top with fresh basil","duration":1}
                    ]""",
                    prepTime = 10,
                    cookTime = 15,
                    servings = 2,
                    difficulty = "Easy",
                    cuisine = "Italian",
                    isVegetarian = true,
                    isNutFree = true,
                    isDairyFree = false,
                    isPersonal = false
                )
            )

            sampleRecipes.forEach { recipe ->
                recipeDao.insertRecipe(recipe)
            }
        }
    }
}
