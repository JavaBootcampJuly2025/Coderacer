import { useState, useRef, useCallback } from 'react';
import {useLocation} from "react-router-dom";
import {getRandomProblemWithDifficulty, submitCode} from "../services/apiService";

const useGameModeLogic = () => {
    const location = useLocation();

    const [isLoading, setIsLoading] = useState(false);
    const [title, setTitle] = useState(location.state?.problem.title);
    const [currentProblemId, setCurrentProblemId] = useState(location.state?.problem.id);
    const [description, setDescription] = useState(location.state?.problem.description);
    const [exampleOutput, setExampleOutput] = useState(location.state?.problem.exampleOutputs);
    const [exampleInput, setExampleInput] = useState(location.state?.problem.exampleInputs);
    const [expectedOutput, setExpectedOutput] = useState(null);
    const [actualOutput, setActualOutput] = useState(null);
    const [compilationError, setCompilationError] = useState(null);
    const [userCode, setUserCode] = useState("static void solution(int n, int[] arr) {  }");
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [testsPassed, setTestsPassed] = useState(0);
    const [totalTests, setTotalTests] = useState(0);

    const containerRef = useRef(null);

    // Focus the container (for accessibility)
    const focusContainer = useCallback(() => {
        if (containerRef.current) {
            const textarea = containerRef.current.querySelector('textarea');
            if (textarea) {
                textarea.focus();
            }
        }
    }, []);

    // Handle code input changes
    const handleCodeChange = useCallback((newCode) => {
        setUserCode(newCode);
    }, []);

    const formatOutput = (arr) => {
        return arr.length === 1 ? arr[0] : `[${arr.join(', ')}]`;
    };

    // Handle code submission
    const handleSubmit = useCallback(async () => {
        if (!userCode.trim()) return;
        setIsLoading(true);

        try {
            const codeToSubmit = userCode.replace(/[\r\n]/g, '');
            const response = await submitCode(currentProblemId, codeToSubmit);

            if(response.executionStatus === "SUCCESS") {
                setTestsPassed(response.passedTests);
                setTotalTests(response.totalTests);
                setExpectedOutput(formatOutput(response.expectedOutput));
                setActualOutput(formatOutput(response.actualOutput));
                setCompilationError(null);
                setIsSubmitted(true);
            }
            else {
                setIsSubmitted(false);
                setExpectedOutput(null);
                setActualOutput(null);
                setCompilationError("Error: " + response.executionStatus + " " + response.errorMessage);
            }
        } catch (error) {
            console.error('Error submitting code:', error);
        }
        setIsLoading(false);
    }, [userCode, testsPassed, totalTests, isLoading]);


    // Generate a new prompt
    const generateNewPrompt = useCallback(async () => {
        setIsLoading(true);
        try {
            const problemData = await getRandomProblemWithDifficulty(location.state?.problem.difficulty);
            setIsSubmitted(false);
            setCurrentProblemId(problemData.id);
            setTitle(problemData.title);
            setDescription(problemData.description);
            setExampleOutput(problemData.exampleOutputs);
            setExampleInput(problemData.exampleInputs);
            setCompilationError(null);
            setExpectedOutput(null);
            setActualOutput(null);
            setUserCode("static void solution(int n, int[] arr) {  }");
        } catch (error) {
            console.error('Error loading level:', error);
        }
        setIsLoading(false);
    }, [focusContainer, currentProblemId, title, description, exampleOutput, userCode, exampleInput, isLoading]);

    return {
        title,
        description,
        exampleOutput,
        exampleInput,
        userCode,
        containerRef,
        handleCodeChange,
        handleSubmit,
        generateNewPrompt,
        focusContainer,
        testsPassed,
        totalTests,
        isLoading,
        isSubmitted,
        expectedOutput,
        actualOutput,
        compilationError
    };
};

export default useGameModeLogic;