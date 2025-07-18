import { useLocation } from 'react-router-dom';
import useTypingTest from './useTypingTest';
import { aaaaa } from '../utils/constants';

const useLevelLogic = () => {
    const { state } = useLocation();
    const {
        codeSnippet,
        userInput,
        endTime,
        totalTyped,
        mistakes,
        speedLogRef,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useTypingTest(aaaaa);

    return {
        state,
        codeSnippet,
        userInput,
        endTime,
        totalTyped,
        mistakes,
        speedLogRef,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    };
};

export default useLevelLogic;