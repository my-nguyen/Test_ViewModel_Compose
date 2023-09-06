package com.nguyen.test_viewmodel_compose.data.ui.test

import com.nguyen.test_viewmodel_compose.data.MAX_NO_OF_WORDS
import com.nguyen.test_viewmodel_compose.data.SCORE_INCREASE
import com.nguyen.test_viewmodel_compose.ui.GameViewModel
import com.nguyen.test_viewmodel_compose.data.getUnscrambledWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val SCORE_AFTER_FIRST_CORRECT_ANSWER = SCORE_INCREASE
class GameViewModelTest {
    private val viewModel = GameViewModel()

    // Success path, when updateUserGuess() is called with correct guess word
    @Test
    fun gameViewModel_CorrectWordGuessed_ScoreUpdatedAndErrorFlagUnset() {
        var gameState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameState.currentScrambledWord)

        viewModel.updateUserGuess(unscrambledWord)
        viewModel.checkUserGuess()

        gameState = viewModel.uiState.value
        // Assert that checkUserGuess() method updates isGuessedWordWrong is updated correctly.
        assertFalse(gameState.isGuessedWordWrong)
        // Assert that score is updated correctly.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, gameState.score)
    }

    // Error path, when an incorrect word is passed to viewModel.updateUserGuess()
    @Test
    fun gameViewModel_IncorrectGuess_ErrorFlagSet() {
        val incorrectWord = "and"

        viewModel.updateUserGuess(incorrectWord)
        viewModel.checkUserGuess()

        val currentGameUiState = viewModel.uiState.value
        // Assert that score is unchanged
        assertEquals(0, currentGameUiState.score)
        // Assert that checkUserGuess() method updates isGuessedWordWrong correctly
        assertTrue(currentGameUiState.isGuessedWordWrong)
    }

    // Boundary case to test the initial state of the UI
    @Test
    fun gameViewModel_Initialization_FirstWordLoaded() {
        val gameState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameState.currentScrambledWord)

        // Assert that current word is scrambled.
        assertNotEquals(unscrambledWord, gameState.currentScrambledWord)
        // Assert that current word count is set to 1.
        assertTrue(gameState.currentWordCount == 1)
        // Assert that initially the score is 0.
        assertTrue(gameState.score == 0)
        // Assert that the wrong word guessed is false.
        assertFalse(gameState.isGuessedWordWrong)
        // Assert that game is not over.
        assertFalse(gameState.isGameOver)
    }

    // boundary case is to test the UI state after the user guesses all the words.
    @Test
    fun gameViewModel_AllWordsGuessed_UiStateUpdatedCorrectly() {
        var expectedScore = 0
        var gameState = viewModel.uiState.value
        var unscrambledWord = getUnscrambledWord(gameState.currentScrambledWord)

        repeat(MAX_NO_OF_WORDS) {
            viewModel.updateUserGuess(unscrambledWord)
            viewModel.checkUserGuess()
            gameState = viewModel.uiState.value
            unscrambledWord = getUnscrambledWord(gameState.currentScrambledWord)
            expectedScore += SCORE_INCREASE
            // Assert that after each correct answer, score is updated correctly.
            assertEquals(expectedScore, gameState.score)
        }

        // Assert that after all questions are answered, the current word count is up-to-date.
        assertEquals(MAX_NO_OF_WORDS, gameState.currentWordCount)
        // Assert that after 10 questions are answered, the game is over.
        assertTrue(gameState.isGameOver)
    }

    // Coverage: without the following test, all the above tests will cover:
    // * 7 out of 8 methods
    // * 39 out of 41 lines of code
    // because it doesn't test GameViewModel.skipWord()
    @Test
    fun gameViewModel_WordSkipped_ScoreUnchangedAndWordCountIncreased() {
        var gameState = viewModel.uiState.value
        val unscrambledWord = getUnscrambledWord(gameState.currentScrambledWord)

        viewModel.updateUserGuess(unscrambledWord)
        viewModel.checkUserGuess()

        gameState = viewModel.uiState.value
        val lastWordCount = gameState.currentWordCount
        viewModel.skipWord()
        gameState = viewModel.uiState.value

        // Assert that score remains unchanged after word is skipped.
        assertEquals(SCORE_AFTER_FIRST_CORRECT_ANSWER, gameState.score)
        // Assert that word count is increased by 1 after word is skipped.
        assertEquals(lastWordCount + 1, gameState.currentWordCount)
    }
}