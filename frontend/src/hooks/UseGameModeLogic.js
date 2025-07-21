import { useState, useRef, useCallback } from 'react';

// Sample coding prompts for different difficulty levels
const CODING_PROMPTS = [
    {
        id: 1,
        difficulty: 'Easy',
        prompt: 'Write a function in Java that calculates the sum of two integers.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'int', '+', 'return']
    },
    {
        id: 2,
        difficulty: 'Easy',
        prompt: 'Create a Java method that prints "Hello, World!" to the console.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'void', 'main', 'System.out.println']
    },
    {
        id: 3,
        difficulty: 'Medium',
        prompt: 'Write a Java method to reverse a given string.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'String', 'StringBuilder', 'reverse', 'return']
    },
    {
        id: 4,
        difficulty: 'Medium',
        prompt: 'Implement a Java function that checks if a number is prime.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'boolean', 'for', '%', 'return']
    },
    {
        id: 5,
        difficulty: 'Hard',
        prompt: 'Write a Java method that implements binary search on a sorted array of integers.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'int', 'while', 'mid', 'return']
    },
    {
        id: 6,
        difficulty: 'Hard',
        prompt: 'Create a Java class representing a simple BankAccount with methods for deposit and withdrawal.',
        language: 'java',
        expectedKeywords: ['public', 'class', 'private', 'double', 'deposit', 'withdraw', 'balance']
    }
];

const useGameModeLogic = () => {
    const [currentPrompt, setCurrentPrompt] = useState(CODING_PROMPTS[0].prompt);
    const [currentPromptData, setCurrentPromptData] = useState(CODING_PROMPTS[0]);
    const [userCode, setUserCode] = useState('');
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [submissionResult, setSubmissionResult] = useState(null);

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

    // Simple code evaluation based on keywords and basic structure
    const evaluateCode = useCallback((code, promptData) => {
        if (!code.trim()) {
            return {
                success: false,
                message: 'Please write some code before submitting.'
            };
        }

        const codeLines = code.toLowerCase().split('\n');
        const codeText = code.toLowerCase();

        // Check for expected keywords
        const foundKeywords = promptData.expectedKeywords.filter(keyword =>
            codeText.includes(keyword.toLowerCase())
        );

        const keywordScore = foundKeywords.length / promptData.expectedKeywords.length;

        // Basic structure checks
        let structureScore = 0;

        // Check for function/method declaration
        if (promptData.language === 'java' && (codeText.includes('public') || codeText.includes('private'))) {
            structureScore += 0.3;
        } else if (promptData.language === 'python' && codeText.includes('def ')) {
            structureScore += 0.3;
        } else if (promptData.language === 'javascript' && (codeText.includes('function') || codeText.includes('=>'))) {
            structureScore += 0.3;
        }

        // Check for return statement
        if (codeText.includes('return')) {
            structureScore += 0.3;
        }

        // Check for basic logic (loops, conditionals)
        if (codeText.includes('for') || codeText.includes('while') || codeText.includes('if')) {
            structureScore += 0.2;
        }

        // Check for proper brackets/braces
        const openBraces = (code.match(/\{/g) || []).length;
        const closeBraces = (code.match(/\}/g) || []).length;
        const openParens = (code.match(/\(/g) || []).length;
        const closeParens = (code.match(/\)/g) || []).length;

        if (openBraces === closeBraces && openParens === closeParens) {
            structureScore += 0.2;
        }

        const totalScore = (keywordScore * 0.6) + (structureScore * 0.4);

        if (totalScore >= 0.7) {
            return {
                success: true,
                message: `Great job! Your code looks good. Score: ${Math.round(totalScore * 100)}%`
            };
        } else if (totalScore >= 0.4) {
            return {
                success: false,
                message: `Good attempt! Try to include more of the required elements. Score: ${Math.round(totalScore * 100)}%`
            };
        } else {
            return {
                success: false,
                message: `Keep trying! Make sure your code addresses the prompt requirements. Score: ${Math.round(totalScore * 100)}%`
            };
        }
    }, []);

    // Handle code submission
    const handleSubmit = useCallback(() => {
        if (!userCode.trim() || isSubmitted) return;

        const result = evaluateCode(userCode, currentPromptData);
        setSubmissionResult(result);
        setIsSubmitted(true);
    }, [userCode, isSubmitted, currentPromptData, evaluateCode]);

    // Generate a new prompt
    const generateNewPrompt = useCallback(() => {
        const randomIndex = Math.floor(Math.random() * CODING_PROMPTS.length);
        const newPromptData = CODING_PROMPTS[randomIndex];

        setCurrentPrompt(newPromptData.prompt);
        setCurrentPromptData(newPromptData);
        setUserCode('');
        setIsSubmitted(false);
        setSubmissionResult(null);

        // Focus the input area after generating new prompt
        setTimeout(() => {
            focusContainer();
        }, 100);
    }, [focusContainer]);

    return {
        currentPrompt,
        currentPromptData,
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
