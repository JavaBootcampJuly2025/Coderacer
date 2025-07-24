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
        isSubmitted
    } = useGameModeLogic();

    const navigate = useNavigate();

    const handleBackToHome = () => {
        navigate('/home');
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

                    {/* Right Side - Expected Output, Score, and Controls (1/3 width) */}
                    <div className="right-panel flex flex-col col-span-1 h-full">
                        {/* Expected Console Output */}
                        <div className="expected-output-section mb-8 h-64">
                            <h3 className="text-xl font-bold text-[var(--text)] mb-3">Example Console Input</h3>
                            <div className="expected-output-content flex flex-col gap-4 h-full">
                                <pre className="flex-1 text-base text-[var(--text)] font-mono whitespace-pre-wrap p-4 bg-[var(--sliderhover)] rounded-lg border-2 border-[var(--accent)] strong-shadow overflow-y-auto">
                                    {exampleInput || 'No example input available for this challenge.'}
                                </pre>
                                <h3 className="text-xl font-bold text-[var(--text)] mb-3">Example Console Output</h3>
                                <pre className="flex-1 text-base text-[var(--text)] font-mono whitespace-pre-wrap p-4 bg-[var(--sliderhover)] rounded-lg border-2 border-[var(--accent)] strong-shadow overflow-y-auto">
                                </pre>
                            </div>
                        </div>


                        {/* Submission Result - Always visible, static size */}
                        <div className="result-section mt-6 h-40"> {}
                            <div className={`rounded-lg p-4 bg-[var(--sliderhover)] border border-[var(--border-gray)] rounded-2xl shadow-lg h-full flex flex-col`}>
                                <h3 className={`font-bold mb-2 text-[var(--text)]`}>Your Score</h3> {}
                                <div className="flex-1 flex flex-col justify-center">
                                    {isSubmitted ? (
                                        <p className="text-xl text-center text-[var(--accent)] font-semibold">
                                            {testsPassed} / {totalTests} Tests Passed
                                        </p>
                                    ) : (
                                        <p className="text-base text-[var(--text)] text-center">Submit your code to see your score!</p>
                                    )}
                                </div>
                            </div>
                        </div>

                        {/* Action Buttons - Right Side */}
                        <div className="action-buttons-right flex flex-col gap-3 flex-1 mt-6"> {/* Added mt-6 for separation from score box */}
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
