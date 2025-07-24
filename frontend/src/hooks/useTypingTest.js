import { useEffect, useRef, useState } from 'react';

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

    // Split code snippet into words and initialize word states
    const words = codeSnippet.split(/(\s+)/); // Keep whitespace as separate elements

    useEffect(() => {
        if (codeSnippet) {
            const initialStates = words.map(() => ({
                status: 'untyped', // 'untyped', 'correct', 'incorrect', 'extra'
                userText: '',
                hasError: false
            }));
            setWordStates(initialStates);
        }
    }, [codeSnippet]);

    // Focus the container on mount
    useEffect(() => {
        if (containerRef.current) {
            try {
                containerRef.current.focus();
                const range = document.createRange();
                const selection = window.getSelection();
                if (containerRef.current.firstChild) {
                    range.setStart(containerRef.current.firstChild, 0);
                    range.collapse(true);
                    selection.removeAllRanges();
                    selection.addRange(range);
                }
            } catch (error) {
                console.error('Error focusing container:', error);
            }
        }
    }, []);

    const handleKeyDown = (e) => {
        if (endTime || isCompleted.current || !codeSnippet) return;

        if (e.key.length === 1 || e.key === 'Backspace' || e.key === 'Enter' || e.key === 'Tab' || e.key === ' ') {
            e.preventDefault();

            const now = Date.now();
            const elapsedSec = startTime ? (now - startTime) / 1000 : 0;

            if (!startTime && userInput.length === 0 && e.key !== 'Backspace') {
                setStartTime(now);
                console.log('Start time set:', now);
            }

            // Handle backspace
            if (e.key === 'Backspace') {
                if (currentWordInput.length > 0) {
                    const newWordInput = currentWordInput.slice(0, -1);
                    setCurrentWordInput(newWordInput);

                    // Update word state
                    setWordStates(prev => {
                        const newStates = [...prev];
                        if (newStates[currentWordIndex]) {
                            newStates[currentWordIndex] = {
                                ...newStates[currentWordIndex],
                                userText: newWordInput,
                                status: newWordInput.length === 0 ? 'untyped' :
                                    newWordInput === words[currentWordIndex] ? 'correct' :
                                        newWordInput.length > words[currentWordIndex].length ? 'extra' : 'incorrect'
                            };
                        }
                        return newStates;
                    });
                } else if (currentWordIndex > 0) {
                    // Go back to previous word if current word is empty
                    const prevWordIndex = currentWordIndex - 1;
                    setCurrentWordIndex(prevWordIndex);
                    setCurrentWordInput(wordStates[prevWordIndex]?.userText || '');
                }
                return;
            }

            // Handle space key - move to next word
            if (e.key === ' ') {
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

                // Move to next non-whitespace word
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

                setTotalTyped(prev => prev + 1);
                return;
            }

            // Handle regular character input
            const charsToAdd = e.key === 'Enter' ? '\n' : e.key === 'Tab' ? '    ' : e.key;
            const newWordInput = currentWordInput + charsToAdd;
            setCurrentWordInput(newWordInput);

            const charsAdded = e.key === 'Tab' ? 4 : 1;
            setTotalTyped(prev => prev + charsAdded);

            // Update word state
            setWordStates(prev => {
                const newStates = [...prev];
                if (newStates[currentWordIndex]) {
                    const currentWord = words[currentWordIndex];
                    let status = 'incorrect';

                    if (newWordInput === currentWord) {
                        status = 'correct';
                    } else if (newWordInput.length > currentWord.length) {
                        status = 'extra';
                    } else if (currentWord.startsWith(newWordInput)) {
                        status = 'partial'; // Partially correct
                    }

                    newStates[currentWordIndex] = {
                        ...newStates[currentWordIndex],
                        userText: newWordInput,
                        status: status
                    };
                }
                return newStates;
            });

            // Log speed data every second
            if (elapsedSec >= lastLoggedTime.current + 1) {
                logSpeedData(elapsedSec, now);
            }

            // Update userInput for compatibility
            const fullUserInput = wordStates.map((state, idx) =>
                idx < currentWordIndex ? state.userText :
                    idx === currentWordIndex ? newWordInput : ''
            ).join('');
            setUserInput(fullUserInput);
        }
    };

    const completeTypingTest = (now, elapsedSec) => {
        if (!isCompleted.current) {
            isCompleted.current = true;
            setEndTime(now);
            logSpeedData(elapsedSec, now);
            console.log('Typing completed! endTime set to:', now);
        }
    };

    const logSpeedData = (elapsedSec, now) => {
        const correctChars = wordStates.reduce((acc, state, idx) => {
            if (idx < currentWordIndex && state.status === 'correct') {
                return acc + words[idx].length;
            }
            return acc;
        }, 0);

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

    const calculateCPM = () => {
        if (!startTime || !endTime || !codeSnippet) return 0;
        const duration = (endTime - startTime) / 1000 / 60;
        return duration > 0 ? Math.round(codeSnippet.length / duration) : 0;
    };

    const calculateAccuracy = () => {
        if (totalTyped === 0) return 100;
        return Math.max(0, Math.round(((totalTyped - mistakes) / totalTyped) * 100));
    };

    const focusContainer = () => {
        try {
            containerRef.current?.focus();
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
        // New word-based properties
        words,
        currentWordIndex,
        currentWordInput,
        wordStates,
    };
};

export default useTypingTest;