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

    // Handle code submission
    const handleSubmit = useCallback(async () => {
        if (!userCode.trim()) return;
        setIsLoading(true);

        try {
            const codeToSubmit = userCode.replace(/[\r\n]/g, '');
            const response = await submitCode(currentProblemId, codeToSubmit);

            setTestsPassed(response.passedTests);
            setTotalTests(response.totalTests);
            setIsSubmitted(true);
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
    };
};

export default useGameModeLogic;