import { useLocation } from 'react-router-dom';
import useTypingTest from './useTypingTest';
import { Lorem } from '../utils/constants';
import { useLevelContext } from '../context/LevelContext';

const useLevelLogic = () => {
    const { state } = useLocation();
    const {
        codeSnippet,
        userInput,
        endTime,
        startTime,
        totalTyped,
        mistakes,
        speedLog,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
        // New word-based properties
        words,
        currentWordIndex,
        currentWordInput,
        wordStates,
    } = useTypingTest(Lorem);

    const { saveSession } = useLevelContext() || {};

    return {
        state,
        codeSnippet,
        userInput,
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
        saveSession: () => saveSession(speedLog, endTime, totalTyped, mistakes, userInput, codeSnippet),
        // New word-based properties
        words,
        currentWordIndex,
        currentWordInput,
        wordStates,
    };
};

export default useLevelLogic;
