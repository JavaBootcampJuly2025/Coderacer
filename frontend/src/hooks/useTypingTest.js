import { useEffect, useRef, useState } from 'react';
import { useLocation } from "react-router-dom";

const useTypingTest = (initialCodeSnippet = '') => {
    const [codeSnippet, setCodeSnippet] = useState(initialCodeSnippet);
    const [userInput, setUserInput] = useState('');
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [totalTyped, setTotalTyped] = useState(0);
    const [mistakes, setMistakes] = useState(0);
    const [speedLog, setSpeedLog] = useState([]);
    const [currentWordIndex, setCurrentWordIndex] = useState(0);
    const [currentWordInput, setCurrentWordInput] = useState('');
    const [wordStates, setWordStates] = useState([]); // Track state of each word
    const containerRef = useRef(null);
    const isCompleted = useRef(false);
    const lastLoggedTime = useRef(0);

    const temp = useLocation().state;

    // Split code snippet into words and initialize word states
    // This needs to be inside a useEffect or memoized if codeSnippet changes
    // to prevent re-splitting on every render.
    const words = codeSnippet.split(/(\s+)/); // Keep whitespace as separate elements

    useEffect(() => {
        if (codeSnippet) {
            const initialStates = words.map(() => ({
                status: 'untyped', // 'untyped', 'correct', 'incorrect', 'extra', 'partial'
                userText: '',
                hasError: false
            }));
            setWordStates(initialStates);
        }
    }, [codeSnippet, words.length]); // Added words.length to dependency array to re-initialize if words change

    // Focus the container on mount
    useEffect(() => {
        console.log(temp);
        if (containerRef.current) {
            try {
                containerRef.current.focus();
                // The selection range logic might be problematic for non-contenteditable elements
                // and might not be necessary if the focus is purely for keyboard input.
                // It's generally used for text fields or contenteditable divs.
                // If you are relying on the browser's default cursor behavior for a div,
                // this might interfere or not work as expected.
                // Commenting out for now, re-enable if absolutely necessary and test thoroughly.
                /*
                const range = document.createRange();
                const selection = window.getSelection();
                if (containerRef.current.firstChild) {
                    range.setStart(containerRef.current.firstChild, 0);
                    range.collapse(true);
                    selection.removeAllRanges();
                    selection.addRange(range);
                }
                */
            } catch (error) {
                console.error('Error focusing container:', error);
            }
        }
    }, []);

    const completeTypingTest = (now, elapsedSec) => {
        if (!isCompleted.current) {
            isCompleted.current = true;
            setEndTime(now);
            logSpeedData(elapsedSec, now);
            console.log('Typing completed! endTime set to:', now);
        }
    };

    const logSpeedData = (elapsedSec, now) => {
        // Calculate correct characters based on words that are explicitly marked 'correct'
        const correctChars = wordStates.reduce((acc, state, idx) => {
            // Only count characters for words that are fully correct and have been processed (i.e., not the current word)
            if (idx < currentWordIndex && state.status === 'correct') {
                return acc + words[idx].length;
            }
            return acc;
        }, 0);

        // Calculate total characters typed up to the current point
        const totalChars = wordStates.reduce((acc, state) => acc + state.userText.length, 0);

        const rawWpm = elapsedSec > 0 ? Math.round((totalChars / 5 / elapsedSec) * 60) : 0;
        const accurateWpm = elapsedSec > 0 ? Math.round((correctChars / 5 / elapsedSec) * 60) : 0;

        setSpeedLog((prev) => [
            ...prev,
            {
                time: Math.floor(elapsedSec),
                rawWpm,
                accurateWpm,
                startTime: startTime || now,
            },
        ]);
        lastLoggedTime.current = Math.floor(elapsedSec);
    };

    const handleKeyDown = (e) => {
        if (endTime || isCompleted.current || !codeSnippet) return;

        // Check for specific keys to prevent default behavior
        if (e.key.length === 1 || e.key === 'Backspace' || e.key === 'Enter' || e.key === 'Tab' || e.key === ' ') {
            e.preventDefault();

            const now = Date.now();
            const elapsedSec = startTime ? (now - startTime) / 1000 : 0;

            if (!startTime && userInput.length === 0 && e.key !== 'Backspace') {
                setStartTime(now);
                console.log('Start time set:', now);
            }

            // Handle Backspace
            if (e.key === 'Backspace') {
                if (currentWordInput.length > 0) {
                    const newWordInput = currentWordInput.slice(0, -1);
                    setCurrentWordInput(newWordInput);

                    setWordStates(prev => {
                        const newStates = [...prev];
                        if (newStates[currentWordIndex]) {
                            const currentWord = words[currentWordIndex];
                            let status = 'untyped';
                            let hasError = false;

                            if (newWordInput.length > 0) {
                                if (currentWord.startsWith(newWordInput)) {
                                    status = 'partial';
                                } else {
                                    status = 'incorrect';
                                    hasError = true;
                                }
                            }

                            newStates[currentWordIndex] = {
                                ...newStates[currentWordIndex],
                                userText: newWordInput,
                                status: status,
                                hasError: hasError
                            };
                        }
                        return newStates;
                    });
                } else if (currentWordIndex > 0) {
                    // Go back to previous word if current word is empty
                    let prevWordIndex = currentWordIndex - 1;
                    // Skip back over whitespace words
                    while (prevWordIndex >= 0 && /^\s+$/.test(words[prevWordIndex])) {
                        prevWordIndex--;
                    }

                    if (prevWordIndex >= 0) {
                        setCurrentWordIndex(prevWordIndex);
                        setCurrentWordInput(wordStates[prevWordIndex]?.userText || '');
                        // Optionally, revert the status of the previous word if you want to allow re-typing
                        setWordStates(prev => {
                            const newStates = [...prev];
                            if (newStates[prevWordIndex]) {
                                newStates[prevWordIndex] = {
                                    ...newStates[prevWordIndex],
                                    status: 'untyped', // Or 'partial' based on its current content
                                    hasError: false
                                };
                            }
                            return newStates;
                        });
                    }
                }
                setTotalTyped(prev => Math.max(0, prev - 1)); // Decrement total typed chars
                return; // Exit handler after backspace
            }

            // Handle Space, Enter, Tab
            if (e.key === ' ' || e.key === 'Enter' || e.key === 'Tab') {
                // Mark current word as complete and move to next
                setWordStates(prev => {
                    const newStates = [...prev];
                    if (newStates[currentWordIndex]) {
                        const currentWord = words[currentWordIndex];
                        const hasError = currentWordInput !== currentWord;
                        newStates[currentWordIndex] = {
                            ...newStates[currentWordIndex],
                            userText: currentWordInput,
                            status: hasError ? 'incorrect' : 'correct',
                            hasError: hasError
                        };
                        if (hasError) {
                            setMistakes(prev => prev + 1);
                        }
                    }
                    return newStates;
                });

                // Move to next non-whitespace word, handling multiple spaces/newlines
                let nextIndex = currentWordIndex + 1;
                while (nextIndex < words.length && /^\s+$/.test(words[nextIndex])) {
                    nextIndex++;
                }

                if (nextIndex < words.length) {
                    setCurrentWordIndex(nextIndex);
                    setCurrentWordInput('');
                } else {
                    // Completed all words
                    completeTypingTest(now, elapsedSec);
                }

                setTotalTyped(prev => prev + 1); // Account for the space/enter/tab character
                return; // Exit handler after processing space/enter/tab
            }

            // Handle regular character input
            const charsToAdd = e.key; // For regular keys, e.key is the character
            const newWordInput = currentWordInput + charsToAdd;
            setCurrentWordInput(newWordInput);

            setTotalTyped(prev => prev + 1); // Increment for each character typed

            // Update word state
            setWordStates(prev => {
                const newStates = [...prev];
                if (newStates[currentWordIndex]) {
                    const currentWord = words[currentWordIndex];
                    let status = 'incorrect';
                    let hasError = true;

                    if (currentWord.startsWith(newWordInput)) {
                        status = 'partial'; // Partially correct if the input is a prefix
                        hasError = false;
                        if (newWordInput.length === currentWord.length) {
                            if (newWordInput === currentWord) {
                                status = 'correct'; // Fully correct if input matches the word
                            } else {
                                status = 'incorrect'; // Should not happen if startsWith is true and lengths match
                                hasError = true;
                            }
                        }
                    } else if (newWordInput.length > currentWord.length) {
                        status = 'extra'; // Typed more characters than the word has
                        hasError = true;
                    }


                    newStates[currentWordIndex] = {
                        ...newStates[currentWordIndex],
                        userText: newWordInput,
                        status: status,
                        hasError: hasError
                    };
                }
                return newStates;
            });

            // Log speed data every second
            if (elapsedSec >= lastLoggedTime.current + 1) {
                logSpeedData(elapsedSec, now);
            }

            // Update userInput for compatibility with the old rendering logic (if still used)
            // This re-constructs the full userInput string from wordStates.
            // If TypingArea completely moves to word-based rendering, this might become less critical.
            const fullUserInput = wordStates.map((state, idx) => {
                if (idx < currentWordIndex) {
                    return state.userText;
                } else if (idx === currentWordIndex) {
                    return newWordInput;
                }
                return ''; // For words not yet reached
            }).join('');
            setUserInput(fullUserInput);
        }
    };


    const calculateCPM = () => {
        if (!startTime || !endTime || !codeSnippet) return 0;
        const duration = (endTime - startTime) / 1000 / 60; // Duration in minutes
        // CPM should be based on *correct* characters typed
        const correctCharsTyped = wordStates.reduce((acc, state, idx) => {
            if (state.status === 'correct') {
                return acc + words[idx].length;
            }
            return acc;
        }, 0);
        return duration > 0 ? Math.round(correctCharsTyped / duration) : 0;
    };

    const calculateAccuracy = () => {
        if (totalTyped === 0) return 0;
        // The `mistakes` state is updated when a word is completed incorrectly.
        // We need to refine `totalTyped` to represent chars attempted, and `mistakes`
        // to be chars incorrect. This current implementation of `mistakes` counts
        // incorrect *words*, not incorrect *characters*.

        // For character-level accuracy:
        let correctCharacters = 0;
        let totalAttemptedCharacters = 0;

        wordStates.forEach((wordState, idx) => {
            const originalWord = words[idx];
            const userTyped = wordState.userText;

            totalAttemptedCharacters += userTyped.length; // Count all characters user tried to type for this word

            // Compare character by character for correctness
            for (let i = 0; i < Math.min(originalWord.length, userTyped.length); i++) {
                if (originalWord[i] === userTyped[i]) {
                    correctCharacters++;
                }
            }
        });

        // If the user typed extra characters, they are considered incorrect
        const extraCharacters = wordStates.reduce((acc, state, idx) => {
            const originalWord = words[idx];
            const userTyped = state.userText;
            if (userTyped.length > originalWord.length) {
                return acc + (userTyped.length - originalWord.length);
            }
            return acc;
        }, 0);

        // This calculation is tricky with word-based input.
        // A simpler way for accuracy might be (correct words / total words) or (correct chars / (correct chars + incorrect chars + extra chars))
        // Let's use correct characters / total attempted characters (including extra chars)

        const finalTotalTyped = totalTyped; // This counts all key presses including spaces/enters/tabs
        const finalMistakes = mistakes; // This counts incorrect words completed

        // For a more robust character-level accuracy:
        let accuratelyTypedChars = 0;
        let totalCharsInSnippet = 0;

        words.forEach((word, idx) => {
            totalCharsInSnippet += word.length;
            const userWord = wordStates[idx]?.userText || '';
            for (let i = 0; i < Math.min(word.length, userWord.length); i++) {
                if (word[i] === userWord[i]) {
                    accuratelyTypedChars++;
                }
            }
        });

        // Consider 'mistakes' as characters that were incorrect or extra
        const totalPossibleChars = words.join('').length;
        const actualTypedChars = userInput.length; // Or sum of all wordState.userText lengths

        // A common formula for accuracy: (Correct Chars) / (Correct Chars + Incorrect Chars + Skipped Chars + Extra Chars)
        // Let's refine based on the wordStates and currentWordInput
        let totalCorrectChars = 0;
        let totalExtraChars = 0;

        wordStates.forEach((state, idx) => {
            const originalWord = words[idx];
            const userTypedWord = state.userText;

            for (let i = 0; i < Math.min(originalWord.length, userTypedWord.length); i++) {
                if (originalWord[i] === userTypedWord[i]) {
                    totalCorrectChars++;
                }
            }
            if (userTypedWord.length > originalWord.length) {
                totalExtraChars += (userTypedWord.length - originalWord.length);
            }
        });

        const totalCharsConsidered = codeSnippet.length + totalExtraChars;

        return totalCharsConsidered > 0 ? Math.max(0, Math.round((totalCorrectChars / totalCharsConsidered) * 100)) : 0;
    };


    const focusContainer = () => {
        try {
            containerRef.current?.focus();
            // Re-adding the selection range logic here as well if needed for cursor placement
            // For general focus on a div for key events, this is often not strictly necessary.
            /*
            const range = document.createRange();
            const selection = window.getSelection();
            if (containerRef.current.firstChild) {
                range.setStart(containerRef.current.firstChild, 0);
                range.collapse(true);
                selection.removeAllRanges();
                selection.addRange(range);
            }
            */
        } catch (error) {
            console.error('Error focusing container:', error);
        }
    };

    // Reset function to restart the typing test
    const reset = () => {
        setUserInput('');
        setStartTime(null);
        setEndTime(null);
        setTotalTyped(0);
        setMistakes(0);
        setSpeedLog([]);
        setCurrentWordIndex(0);
        setCurrentWordInput('');
        // Re-initialize wordStates based on the current codeSnippet
        setWordStates(words.map(() => ({
            status: 'untyped',
            userText: '',
            hasError: false
        })));
        isCompleted.current = false;
        lastLoggedTime.current = 0;
        focusContainer();
    };

    return {
        codeSnippet,
        setCodeSnippet,
        userInput,
        setUserInput,
        startTime,
        endTime,
        totalTyped,
        mistakes,
        speedLog,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
        reset,
        words,
        currentWordIndex,
        currentWordInput,
        wordStates,
    };
};

export default useTypingTest;