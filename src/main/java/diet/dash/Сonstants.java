package diet.dash;

public class Сonstants {
    public final String prompt =
            "Create a 1-day meal plan based on the input below.\nReturn the answer strictly as valid JSON" +
                    ", with no text outside and no comments inside.\n\nJSON structure:\n\n{ \n  " +
                    "\"dailyCalories\": number, \n  \"macros\": { \"protein\": number, \"carbs\": number, " +
                    "\"fat\": number }, \n  \"meals\": [ \n    { \"name\": string, \"items\": [ \n      " +
                    "{ \"product\": string, \"amount\": string, \"calories\": number, \"protein\": number, " +
                    "\"carbs\": number, \"fat\": number } \n    ] } \n  ], \n  \"notes\": string, \n  " +
                    "\"estimatedCostEUR\": number \n}\n\n\nRules:\n\ndailyCalories and macros must match the " +
                    "target values within ±5%.\n\nDistribute calories and macros evenly across meals, with " +
                    "extra emphasis on protein in the morning and post-workout.\n\nUse simple, widely available " +
                    "ingredients.\n\nestimatedCostEUR must be the approximate cost of ingredients in a " +
                    "typical European supermarket.\n\nApply user comments only if they are clearly relevant " +
                    "to diet preferences, restrictions, or allergies (e.g., “no fish”, “vegetarian”, " +
                    "“no dairy”).\n\nIgnore irrelevant or nonsensical comments.\n\nInput data:\nSex: " +
                    "Male; Age: 24; Height: 190 cm; Weight: 90 kg.\nTarget calories: 3100 kcal.\nTarget " +
                    "macros: protein 190 g, carbs 370 g, fat 95 g.\nMeals per day: 4.\nComments: “no fish”.";
}
