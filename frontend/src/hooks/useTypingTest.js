import {useEffect, useRef, useState} from 'react';

const useTypingTest = (initialCodeSnippet = '') => {
    const [codeSnippet, setCodeSnippet] = useState(initialCodeSnippet);
    const [userInput, setUserInput] = useState('');
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [totalTyped, setTotalTyped] = useState(0);
    const [mistakes, setMistakes] = useState(0);
    const [speedLog, setSpeedLog] = useState([]);
    const containerRef = useRef(null);
    const isCompleted = useRef(false);

    // Focus the container on the mount
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

        if (e.key.length === 1 || e.key === 'Backspace' || e.key === 'Enter' || e.key === 'Tab') {
            e.preventDefault();

            const now = Date.now();
            const elapsedSec = startTime ? (now - startTime) / 1000 : 0;

            if (!startTime && userInput.length === 0 && e.key !== 'Backspace') {
                setStartTime(now);
                console.log('Start time set:', now);
            }

            let newInput = userInput;
            if (e.key === 'Backspace') {
                newInput = userInput.slice(0, -1);
            } else {
                const charsToAdd = e.key === 'Enter' ? '\n' : e.key === 'Tab' ? '  ' : e.key;
                newInput = userInput + charsToAdd;
                const charsAdded = e.key === 'Tab' ? 2 : 1;

                setTotalTyped((prev) => {
                    const newTotal = prev + charsAdded;
                    console.log('Total typed updated:', newTotal);
                    return newTotal;
                });

                // Only check for mistakes if within codeSnippet bounds
                if (userInput.length < codeSnippet.length) {
                    const expectedChar = codeSnippet[userInput.length];
                    const isMistake = charsToAdd !== expectedChar;

                    if (isMistake) {
                        setMistakes((prev) => prev + 1);
                    } else {
                        const correctChars = [...newInput].filter((ch, i) => {
                            return i < codeSnippet.length && ch === codeSnippet[i];
                        }).length;
                        const cpm = elapsedSec > 0 ? Math.round((correctChars / elapsedSec) * 60) : 0;
                        setSpeedLog((prev) => [...prev, { time: elapsedSec.toFixed(1), cpm }]);
                    }
                }
            }

            setUserInput(newInput);
            console.log('userInput updated:', newInput, 'codeSnippet:', codeSnippet, 'length match:', newInput.length >= codeSnippet.length);

            // In the handleKeyDown function:
            if (!isCompleted.current && newInput === codeSnippet) {
                isCompleted.current = true;
                const now = Date.now();
                setEndTime(now);
                console.log('Typing completed! endTime set to:', now); // Add this log
            }
        }
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
        isCompleted.current = false;
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
    };
};

export default useTypingTest;