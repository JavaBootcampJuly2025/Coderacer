import { useState, useEffect, useRef } from 'react';

const useTypingTest = (initialCodeSnippet) => {
    const [codeSnippet, setCodeSnippet] = useState(initialCodeSnippet);
    const [userInput, setUserInput] = useState('');
    const [startTime, setStartTime] = useState(null);
    const [endTime, setEndTime] = useState(null);
    const [totalTyped, setTotalTyped] = useState(0);
    const [mistakes, setMistakes] = useState(0);
    const [speedLog, setSpeedLog] = useState([]);
    const containerRef = useRef(null);

    useEffect(() => {
        if (containerRef.current) {
            containerRef.current.focus();
            const range = document.createRange();
            const selection = window.getSelection();
            if (containerRef.current.firstChild) {
                range.setStart(containerRef.current.firstChild, 0);
                range.collapse(true);
                selection.removeAllRanges();
                selection.addRange(range);
            }
        }
    }, []);

    const handleKeyDown = (e) => {
        if (endTime) return;

        if (e.key.length === 1 || e.key === 'Backspace' || e.key === 'Enter' || e.key === 'Tab') {
            e.preventDefault();

            const now = Date.now();
            const elapsedSec = startTime ? (now - startTime) / 1000 : 0;
            let newInput = userInput;

            if (!startTime && userInput.length === 0) {
                setStartTime(now);
            }

            if (e.key === 'Backspace') {
                newInput = userInput.slice(0, -1);
            } else {
                if (e.key === 'Enter') {
                    newInput = userInput + '\n';
                } else if (e.key === 'Tab') {
                    newInput = userInput + '  ';
                } else {
                    newInput = userInput + e.key;
                }

                setTotalTyped((prev) => prev + (e.key === 'Tab' ? 2 : 1));

                const expectedChar = codeSnippet[userInput.length];
                const actualChar = e.key === 'Enter' ? '\n' : e.key === 'Tab' ? '  ' : e.key;
                const isMistake = actualChar !== expectedChar && e.key !== 'Backspace';

                if (isMistake) {
                    setMistakes((prev) => prev + 1);
                } else {
                    const correctChars = [...newInput].filter((ch, i) => ch === codeSnippet[i]).length;
                    const cpm = elapsedSec > 0 ? Math.round((correctChars / elapsedSec) * 60) : 0;
                    setSpeedLog((prev) => [...prev, { time: elapsedSec.toFixed(1), cpm }]);

                    // Check completion within this scope
                    const normalizedInput = newInput.replace(/\s+/g, ' ').trim();
                    const normalizedSnippet = codeSnippet.replace(/\s+/g, ' ').trim();
                    if (normalizedInput === normalizedSnippet || (newInput.length >= codeSnippet.length && !isMistake)) {
                        setEndTime(now);
                        console.log('Typing complete, endTime set:', now); // Debug log
                    }
                }
            }

            setUserInput(newInput);
            console.log('userInput:', newInput, 'codeSnippet:', codeSnippet); // Debug log
        }
    };

    const calculateCPM = () => {
        if (!startTime || !endTime) return 0;
        const duration = (endTime - startTime) / 1000 / 60;
        return Math.round(codeSnippet.length / duration);
    };

    const calculateAccuracy = () => {
        if (totalTyped === 0) return 100;
        return Math.max(0, Math.round(((totalTyped - mistakes) / totalTyped) * 100));
    };

    const focusContainer = () => {
        containerRef.current?.focus();
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
    };
};

export default useTypingTest;