package com.example.model

enum class NavTab(val title: String) {
    LEARN("Tables"),
    PRACTICE("Practice"),
    VISUAL("Visual Grid"),
    PYTHAGORAS("Pythagoras"),
    MASTERY("Mastery & Tips")
}

enum class QuizMode(val displayName: String, val description: String) {
    SPRINT("Speed Sprint", "15 quick questions with combo multipliers"),
    MISSING_FACTOR("Missing Factor", "Find the hidden multiplier (? × 7 = 42)"),
    TIME_ATTACK("60s Blitz", "Answer as many as possible before the clock expires!"),
    DUEL("2-Player Duel", "Face off on one device! First to tap scores.")
}

enum class InputMode {
    MULTIPLE_CHOICE,
    NUMPAD
}

enum class MissingType {
    RESULT,    // a × b = ?
    FACTOR_A,  // ? × b = c
    FACTOR_B   // a × ? = c
}

data class QuizQuestion(
    val factorA: Int,
    val factorB: Int,
    val missingType: MissingType = MissingType.RESULT,
    val options: List<Int>
) {
    val correctAnswer: Int
        get() = when (missingType) {
            MissingType.RESULT -> factorA * factorB
            MissingType.FACTOR_A -> factorA
            MissingType.FACTOR_B -> factorB
        }

    val displayEquation: String
        get() = when (missingType) {
            MissingType.RESULT -> "$factorA × $factorB = ?"
            MissingType.FACTOR_A -> "? × $factorB = ${factorA * factorB}"
            MissingType.FACTOR_B -> "$factorA × ? = ${factorA * factorB}"
        }

    val repeatedAdditionText: String
        get() = List(factorA) { factorB }.joinToString(" + ") + " = ${factorA * factorB}"
}

data class MathTrick(
    val tableNumber: Int,
    val title: String,
    val rule: String,
    val example: String,
    val visualHint: String
)

object MathTricksData {
    val tricks = listOf(
        MathTrick(
            tableNumber = 0,
            title = "The Zero Hero",
            rule = "Any number multiplied by 0 is ALWAYS 0. It vanishes like magic!",
            example = "7 × 0 = 0, 999 × 0 = 0",
            visualHint = "0 groups of anything means you have nothing in your hands!"
        ),
        MathTrick(
            tableNumber = 1,
            title = "The Mirror Rule",
            rule = "Multiplying by 1 never changes a number. The number looks in the mirror and sees itself!",
            example = "8 × 1 = 8, 12 × 1 = 12",
            visualHint = "1 basket with 8 apples contains exactly 8 apples."
        ),
        MathTrick(
            tableNumber = 2,
            title = "Double It Up!",
            rule = "Multiplying by 2 is just adding the number to itself (doubling).",
            example = "6 × 2 = 6 + 6 = 12",
            visualHint = "Think of bicycle wheels or a pair of shoes!"
        ),
        MathTrick(
            tableNumber = 3,
            title = "Double + One More",
            rule = "To multiply by 3, double the number first, then add it one more time.",
            example = "7 × 3 = (7 × 2) + 7 = 14 + 7 = 21",
            visualHint = "Think of a triangle with 3 sides!"
        ),
        MathTrick(
            tableNumber = 4,
            title = "Double-Double",
            rule = "Multiplying by 4 is just doubling twice!",
            example = "6 × 4 -> Double 6 is 12 -> Double 12 is 24!",
            visualHint = "Easy mental shortcut for quick calculation!"
        ),
        MathTrick(
            tableNumber = 5,
            title = "The Clock Trick",
            rule = "All multiples of 5 end in either 0 or 5. Also, 5 × n is half of 10 × n!",
            example = "8 × 5 = half of 80 = 40! (Ends in 0). 7 × 5 = 35 (Ends in 5).",
            visualHint = "Look at an analog clock: 5, 10, 15, 20, 25, 30..."
        ),
        MathTrick(
            tableNumber = 6,
            title = "Even Rhymes & 5s+1",
            rule = "For even numbers: 6 × 4 = 24, 6 × 6 = 36, 6 × 8 = 48 (Notice the last digit matches!). Or do (5 × n) + n.",
            example = "6 × 7 = (5 × 7) + 7 = 35 + 7 = 42",
            visualHint = "Even numbers rhyme with the multiplier!"
        ),
        MathTrick(
            tableNumber = 7,
            title = "The 5-6-7-8 Sequence",
            rule = "Memorize the golden rhyme: 5, 6, 7, 8 -> 56 = 7 × 8! Or break 7 into 5 + 2.",
            example = "7 × 6 = (5 × 6) + (2 × 6) = 30 + 12 = 42",
            visualHint = "7 is the trickiest table, so break it into 5s and 2s!"
        ),
        MathTrick(
            tableNumber = 8,
            title = "Double-Double-Double",
            rule = "Multiplying by 8 is doubling 3 times! (Or 10× minus 2×)",
            example = "7 × 8 -> 7 -> 14 -> 28 -> 56! Or (10 × 7) - (2 × 7) = 70 - 14 = 56",
            visualHint = "An octopus has 8 arms: 3 pairs of doubles!"
        ),
        MathTrick(
            tableNumber = 9,
            title = "The 9s Finger Magic & Sum 9",
            rule = "The digits of 9's multiples always add up to 9! (9, 18: 1+8=9, 27: 2+7=9, 36: 3+6=9...).",
            example = "9 × 7: Tens digit is 7 - 1 = 6. Units digit is 9 - 6 = 3 -> 63!",
            visualHint = "Put 10 fingers out, bend down the 7th finger. 6 fingers on left, 3 on right = 63!"
        ),
        MathTrick(
            tableNumber = 10,
            title = "The Zero Suffix",
            rule = "Just attach a 0 to the end of the number!",
            example = "7 × 10 = 70, 12 × 10 = 120",
            visualHint = "Our entire base-10 number system is built on this!"
        ),
        MathTrick(
            tableNumber = 11,
            title = "Twin Digits",
            rule = "For single digits 1 to 9, repeat the digit twice!",
            example = "11 × 4 = 44, 11 × 7 = 77, 11 × 9 = 99. For 11 × 12: 120 + 12 = 132!",
            visualHint = "Twin numbers standing side-by-side!"
        ),
        MathTrick(
            tableNumber = 12,
            title = "10 + 2 Combo",
            rule = "Multiply by 10, then multiply by 2, and add them together!",
            example = "12 × 8 = (10 × 8) + (2 × 8) = 80 + 16 = 96",
            visualHint = "A dozen eggs in a carton!"
        )
    )
}
