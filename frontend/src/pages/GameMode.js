import React, { useEffect, useRef } from 'react';
import useGameModeLogic from '../hooks/UseGameModeLogic';
import CodeInputArea from '../components/CodeInputArea';
import '../App.css';
import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';

const GameMode = () => {
    const {
        title,
        description,
        userCode,
        exampleOutput,
        exampleInput,
        containerRef,
        handleCodeChange,
        handleSubmit,
        generateNewPrompt,
        focusContainer,
        testsPassed,
        totalTests,
        isLoading,
        isSubmitted,
        testResult // Added to access detailed test results
    } = useGameModeLogic();

    const navigate = useNavigate();

    const handleBackToHome = () => {
        navigate('/home');
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'SUCCESS':
                return 'Success';
            case 'COMPILATION_ERROR':
                return 'Compilation Error';
            case 'RUNTIME_ERROR':
                return 'Runtime Error';
            case 'TIMEOUT':
                return 'Timeout';
            case 'OUTPUT_MISMATCH':
                return 'Output Mismatch';
            default:
                return 'Unknown';
        }
    };

    return (
        <div className="home-wrapper min-h-screen bg-[var(--background)] flex flex-col font-montserrat">
            {isLoading && (
                <div className="fixed inset-0 z-50 bg-black bg-opacity-50 flex items-center justify-center">
                    <div className="flex flex-col items-center gap-4">
                        <div className="h-16 w-16 border-4 border-[var(--accent)] border-t-transparent rounded-full animate-spin"></div>
                    </div>
                </div>
            )}

            <Header />
            <div className="main-body flex-1 flex flex-col p-6">
                {/* Main Content Area - Split Layout */}
                <div className="content-grid flex-1 grid grid-cols-3 gap-6 mb-6">
                    {/* Left Side - Prompt and Code Input (2/3 width) */}
                    <div className="left-panel flex flex-col col-span-2 h-full">
                        {/* Prompt Display Area */}
                        <div className="prompt-section mb-4 h-32">
                            <div className="bg-[var(--sliderhover)] rounded-lg p-4 strong-shadow border-2 border-[var(--accent)] h-full">
                                <h2 className="text-xl font-bold text-[var(--text)] mb-3">{title}</h2>
                                <p className="text-base text-[var(--text)] leading-relaxed overflow-y-auto">
                                    {description}
                                </p>
                            </div>
                        </div>

                        {/* Code Input Area */}
                        <div className="code-input-section flex-1">
                            <CodeInputArea
                                userCode={userCode}
                                containerRef={containerRef}
                                handleCodeChange={handleCodeChange}
                                focusContainer={focusContainer}
                            />
                        </div>
                    </div>

                    {/* Right Side - Compact Examples and Enhanced Results (1/3 width) */}
                    <div className="examples-section mb-4">
                        {/* Compact Example Input/Output - Horizontal Layout */}
                        <div className="bg-[var(--sliderhover)] rounded-lg p-3 border-2 border-[var(--accent)] strong-shadow">
                            <h3 className="text-sm font-bold text-[var(--text)] mb-2">Examples</h3>
                            <div className="flex gap-2 overflow-hidden">
                                <div className="flex-1">
                                    <p className="text-xs font-semibold text-[var(--text)] mb-1">Input:</p>
                                    <pre className="text-xs text-[var(--text)] font-mono bg-[var(--background)] rounded p-1 max-h-20 overflow-auto whitespace-pre-wrap break-words">
                                        {exampleInput || 'No input'}
                                    </pre>
                                </div>
                                <div className="flex-1">
                                    <p className="text-xs font-semibold text-[var(--text)] mb-1">Output:</p>
                                    <pre className="text-xs text-[var(--text)] font-mono bg-[var(--background)] rounded p-1 max-h-20 overflow-auto whitespace-pre-wrap break-words">
                                        {exampleOutput || 'No output'}
                                    </pre>
                                </div>
                            </div>
                        </div>

                        {/* Enhanced Submission Result Display */}
                        <div className="result-section flex-1">
                            <div className="bg-[var(--sliderhover)] rounded-lg p-4 border-2 border-[var(--accent)] strong-shadow h-96 flex flex-col">
                                <h3 className="text-lg font-bold text-[var(--text)] mb-3">Results</h3>

                                {isSubmitted && testResult ? (
                                    <div className="flex-1 flex flex-col gap-3 overflow-y-auto">
                                        <div className="bg-[var(--background)] rounded p-3">
                                            <p className="text-sm font-semibold text-[var(--text)] mb-1">Score:</p>
                                            <p className="text-xl font-bold text-[var(--accent)]">
                                                {testsPassed} / {totalTests} Tests Passed
                                            </p>
                                        </div>

                                        <div className="bg-[var(--background)] rounded p-3">
                                            <p className="text-sm font-semibold text-[var(--text)] mb-1">Status:</p>
                                            <p className={`text-sm font-bold text-[var(--text)]`}>
                                                {getStatusText(testResult.executionStatus)}
                                            </p>
                                        </div>

                                        {testResult.actualOutput && testResult.actualOutput.length > 0 && (
                                            <div className="bg-[var(--background)] rounded p-3 flex-1">
                                                <p className="text-sm font-semibold text-[var(--text)] mb-2">Your Output:</p>
                                                <pre className="text-xs text-[var(--text)] font-mono bg-[var(--sliderhover)] rounded p-2 overflow-y-auto max-h-24 whitespace-pre-wrap">
                                                    {testResult.actualOutput.join('\n')}
                                                </pre>
                                            </div>
                                        )}

                                        {testResult.expectedOutput && testResult.expectedOutput.length > 0 && (
                                            <div className="bg-[var(--background)] rounded p-3">
                                                <p className="text-sm font-semibold text-[var(--text)] mb-2">Expected:</p>
                                                <pre className="text-xs text-[var(--text)] font-mono bg-[var(--sliderhover)] rounded p-2 overflow-y-auto max-h-16 whitespace-pre-wrap">
                                                    {testResult.expectedOutput.join('\n')}
                                                </pre>
                                            </div>
                                        )}

                                        {testResult.errorMessage && (
                                            <div className="bg-[var(--error-background)] rounded p-3">
                                                <p className="text-sm font-semibold text-[var(--error)] mb-2">Error:</p>
                                                <pre className="text-xs text-[var(--error-message)] font-mono bg-[var(--error-box-background)] rounded p-2 overflow-y-auto max-h-16 whitespace-pre-wrap">
                                                    {testResult.errorMessage}
                                                </pre>
                                            </div>
                                        )}
                                    </div>
                                ) : (
                                    <div className="flex-1 flex items-center justify-center">
                                        <p className="text-base text-[var(--text)] text-center">
                                            Submit your code to see detailed results!
                                        </p>
                                    </div>
                                )}
                            </div>
                        </div>


                        {/* Action Buttons */}
                        <div className="action-buttons-right flex flex-col gap-3 mt-4">
                            <button
                                onClick={handleSubmit}
                                disabled={!userCode.trim()}
                                className="w-full px-6 py-3 bg-[var(--background)] border-2 border-[var(--accent)] text-[var(--accent)] font-bold rounded-lg transition-all duration-200 shadow-lg
                                           hover:bg-[var(--accent)] hover:text-[var(--background)] hover:border-[var(--accent)]
                                           disabled:opacity-50 disabled:cursor-not-allowed"
                            >
                                Submit Answer
                            </button>

                            <button
                                onClick={generateNewPrompt}
                                className="w-full px-6 py-3 bg-[var(--background)] border-2 border-[var(--accent)] text-[var(--accent)] font-bold rounded-lg transition-all duration-200 shadow-lg
                                           hover:bg-[var(--accent)] hover:text-[var(--background)] hover:border-[var(--accent)]"
                            >
                                New Challenge
                            </button>

                            <button
                                onClick={handleBackToHome}
                                className="w-full px-6 py-3 bg-[var(--background)] border border-[var(--border-gray)] rounded-2xl shadow-lg text-[var(--accent)] font-bold rounded-lg transition-all duration-200
                                           hover:bg-[var(--accent)] hover:text-[var(--background)] hover:border-[var(--accent)]"
                            >
                                Back to Home
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default GameMode;

