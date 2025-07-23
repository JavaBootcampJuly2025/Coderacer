import { useLocation } from 'react-router-dom';
import useTypingTest from './useTypingTest';
import { aaaaa } from '../utils/constants';
import { useLevelContext } from '../context/LevelContext'; // Ensure correct path

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
        speedLogRef,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useTypingTest(useLocation().state?.level.codeSnippet);

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
        speedLogRef,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
        saveSession: () => saveSession(speedLog, endTime, totalTyped, mistakes, userInput, codeSnippet),
};
};

export default useLevelLogic;