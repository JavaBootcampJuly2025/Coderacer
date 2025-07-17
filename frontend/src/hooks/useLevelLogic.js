import { useLocation } from 'react-router-dom';
import useTypingTest from './useTypingTest';
import { Lorem } from '../utils/constants';

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