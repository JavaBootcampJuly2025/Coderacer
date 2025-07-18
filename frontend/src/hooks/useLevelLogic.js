import { useLocation } from 'react-router-dom';
import useTypingTest from './useTypingTest';
import { Lorem } from '../utils/constants';
import { useLevelContext } from '../context/LevelContext'; // Ensure correct path

const useLevelLogic = () => {
    const { state } = useLocation();
    const {
        codeSnippet,
        userInput,
        endTime,
        totalTyped,
        mistakes,
        speedLog,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useTypingTest(Lorem);

    // Safely access saveSession from context with fallback
    const { saveSession = () => {} } = useLevelContext() || {};

    // Save session data when endTime is set
    if (endTime && speedLog && speedLog.length > 1) {
        saveSession(speedLog, endTime);
    }

    return {
        state,
        codeSnippet,
        userInput,
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

export default useLevelLogic;