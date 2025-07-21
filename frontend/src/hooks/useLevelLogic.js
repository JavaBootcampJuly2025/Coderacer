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
        startTime,
        totalTyped,
        mistakes,
        speedLog,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useTypingTest(useLocation().state?.level.codeSnippet);

    const { saveSession } = useLevelContext() || {};

    // Remove the saveSession call from render phase
    const context = useLevelContext();

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
        saveSession // Expose saveSession to be called by Level component
    };
};

export default useLevelLogic;