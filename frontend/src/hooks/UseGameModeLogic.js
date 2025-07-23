import { useState, useRef, useCallback } from 'react';
import {useLocation} from "react-router-dom";
import {getRandomProblemWithDifficulty} from "../services/apiService";

const useGameModeLogic = () => {
    const location = useLocation();

    const [title, setTitle] = useState(location.state?.problem.title);
    const [description, setDescription] = useState(location.state?.problem.description);
    const [exampleOutput, setExampleOutput] = useState(location.state?.problem.exampleOutputs);
    const [exampleInput, setExampleInput] = useState(location.state?.problem.exampleInputs);
    const [userCode, setUserCode] = useState('');
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [submissionResult] = useState(null);

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
        if (!isSubmitted) {
            setUserCode(newCode);
        }
    }, [isSubmitted]);

    // Handle code submission
    const handleSubmit = useCallback(() => {
        if (!userCode.trim() || isSubmitted) return;
        setIsSubmitted(true);

        // todo implement code compilation on backend
    }, [userCode, isSubmitted]);

    // Generate a new prompt
    const generateNewPrompt = useCallback(async () => {
        try {
            const problemData = await getRandomProblemWithDifficulty(location.state?.problem.difficulty);
            setTitle(problemData.title);
            setDescription(problemData.description);
            setExampleOutput(problemData.exampleOutputs);
            setExampleInput(problemData.exampleInputs);
            setIsSubmitted(false);
        } catch (error) {
            console.error('Error loading level:', error);
        }
    }, [focusContainer, title, description, exampleOutput, isSubmitted, exampleInput]);

    return {
        title,
        description,
        exampleOutput,
        exampleInput,
        userCode,
        isSubmitted,
        submissionResult,
        containerRef,
        handleCodeChange,
        handleSubmit,
        generateNewPrompt,
        focusContainer
    };
};

export default useGameModeLogic;