package com.haiklabs.crowdclash

object Questions {
    val all = listOf(
        q("Name something people do right after waking up", "Check their phone" to 31, "Brush teeth" to 24, "Use the bathroom" to 20, "Drink coffee" to 15, "Hit snooze" to 10),
        q("Name something you bring to the beach", "Towel" to 30, "Sunscreen" to 26, "Swimsuit" to 18, "Umbrella" to 14, "Snacks" to 12),
        q("Name a food that is hard to eat neatly", "Spaghetti" to 30, "Tacos" to 25, "Ribs" to 19, "Ice cream" to 15, "Watermelon" to 11),
        q("Name something people lose around the house", "Keys" to 34, "Phone" to 27, "Remote" to 21, "Socks" to 11, "Glasses" to 7),
        q("Name something that makes a road trip better", "Music" to 32, "Snacks" to 27, "Good company" to 19, "Games" to 13, "Scenery" to 9),
        q("Name a reason someone might be late", "Traffic" to 35, "Overslept" to 28, "Could not find keys" to 15, "Weather" to 12, "Missed the bus" to 10),
        q("Name something found at a birthday party", "Cake" to 36, "Balloons" to 24, "Presents" to 20, "Candles" to 12, "Music" to 8),
        q("Name something people photograph on vacation", "Landmarks" to 29, "Family" to 25, "Food" to 18, "Sunset" to 16, "Hotel" to 12),
        q("Name a job a dog might be good at", "Security" to 31, "Detective" to 24, "Therapist" to 19, "Athlete" to 15, "Food critic" to 11),
        q("Name something people do during a power cut", "Find a flashlight" to 32, "Light candles" to 25, "Check the neighbors" to 18, "Play games" to 14, "Go to sleep" to 11),
        q("Name something you might find under a sofa", "Coins" to 28, "Remote" to 25, "Dust" to 22, "Snacks" to 15, "Toy" to 10),
        q("Name something that gets better with practice", "A sport" to 30, "Cooking" to 24, "Music" to 21, "Public speaking" to 15, "Drawing" to 10)
    )

    private fun q(prompt: String, vararg answers: Pair<String, Int>) =
        SurveyQuestion(prompt, answers.map { SurveyAnswer(it.first, it.second) })
}

