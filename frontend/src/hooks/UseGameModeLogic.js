import { useState, useRef, useCallback } from 'react';

// Sample coding prompts for different difficulty levels (Java only)
const CODING_PROMPTS = [
    {
        id: 1,
        difficulty: 'Easy',
        prompt: 'Write a function in Java that calculates the sum of two integers and prints the result.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'int', '+', 'System.out.println'],
        expectedOutput: `15
20
-3`
    },
    {
        id: 2,
        difficulty: 'Easy',
        prompt: 'Create a Java method that prints "Hello, World!" to the console.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'void', 'main', 'System.out.println'],
        expectedOutput: `Hello, World!`
    },
    {
        id: 3,
        difficulty: 'Medium',
        prompt: 'Write a Java method to reverse a given string and print the result.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'String', 'StringBuilder', 'reverse', 'System.out.println'],
        expectedOutput: `olleh
avaJ
!dlroW`
    },
    {
        id: 4,
        difficulty: 'Medium',
        prompt: 'Implement a Java function that checks if a number is prime and prints true or false.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'boolean', 'for', '%', 'System.out.println'],
        expectedOutput: `true
false
true
false`
    },
    {
        id: 5,
        difficulty: 'Hard',
        prompt: 'Write a Java method that implements binary search and prints the index of the found element.',
        language: 'java',
        expectedKeywords: ['public', 'static', 'int', 'while', 'mid', 'System.out.println'],
        expectedOutput: `2
-1
0
4`
    },
    {
        id: 6,
        difficulty: 'Hard',
        prompt: 'Create a Java class representing a simple BankAccount and print the balance after operations.',
        language: 'java',
        expectedKeywords: ['public', 'class', 'private', 'double', 'deposit', 'withdraw', 'System.out.println'],
        expectedOutput: `100.0
150.0
120.0
120.0`
    }
];

const useGameModeLogic = () => {
    const [currentPrompt, setCurrentPrompt] = useState(CODING_PROMPTS[0].prompt);
    const [currentPromptData, setCurrentPromptData] = useState(CODING_PROMPTS[0]);
    const [expectedOutput, setExpectedOutput] = useState(CODING_PROMPTS[0].expectedOutput);
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

    // Enhanced code evaluation with console output focus
    const evaluateCode = useCallback((code, promptData) => {
        if (!code.trim()) {
            return {
                success: false,
                message: 'Please write some code before submitting.',
                details: {
                    structureScore: 0,
                    outputScore: 0,
                    totalScore: 0
                }
            };
        }

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
        }

        // Check for print statements (important for console output)
        if (codeText.includes('system.out.println') || codeText.includes('system.out.print')) {
            structureScore += 0.4;
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
            structureScore += 0.1;
        }

        // Simplified output scoring based on structure
        const outputScore = keywordScore * 100;

        // Combine scores: 60% structure, 40% output
        const finalStructureScore = Math.round((keywordScore * 0.6 + structureScore * 0.4) * 100);
        const finalOutputScore = Math.round(outputScore);
        const totalScore = Math.round(finalStructureScore * 0.6 + finalOutputScore * 0.4);

        const success = totalScore >= 70;

        let message;
        if (totalScore >= 90) {
            message = 'Excellent work! Your solution should produce the expected output.';
        } else if (totalScore >= 70) {
            message = 'Good job! Your solution meets the requirements.';
        } else if (totalScore >= 50) {
            message = 'Good attempt! Make sure to include print statements for console output.';
        } else {
            message = 'Keep trying! Focus on the required elements and console output.';
        }

        return {
            success,
            message,
            details: {
                structureScore: finalStructureScore,
                outputScore: finalOutputScore,
                totalScore
            }
        };
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
        setExpectedOutput(newPromptData.expectedOutput);
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
        expectedOutput,
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