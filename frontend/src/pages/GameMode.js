import React, { useEffect, useRef } from 'react';
import useGameModeLogic from '../hooks/UseGameModeLogic';
import CodeInputArea from '../components/CodeInputArea';
import '../App.css';
import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';

const GameMode = () => {
    const {
        currentPrompt,
        userCode,
        expectedOutput,
        isSubmitted,
        submissionResult,
        containerRef,
        handleCodeChange,
        handleSubmit,
        generateNewPrompt,
        focusContainer
    } = useGameModeLogic();

    const navigate = useNavigate();

    const handleBackToHome = () => {
        navigate('/home');
    };

    return (
        <div className="home-wrapper min-h-screen bg-[var(--background)] flex flex-col font-montserrat">
            <Header />
            <div className="main-body flex-1 flex flex-col p-6">
                {/* Main Content Area - Split Layout */}
                <div className="content-grid flex-1 grid grid-cols-3 gap-6 mb-6">
                    {/* Left Side - Prompt and Code Input (2/3 width) */}
                    <div className="left-panel flex flex-col col-span-2 h-full">
                        {/* Prompt Display Area */}
                        <div className="prompt-section mb-4 h-32">
                            <div className="bg-[var(--sliderhover)] rounded-lg p-4 strong-shadow border-2 border-[var(--accent)] h-full">
                                <h2 className="text-xl font-bold text-[var(--text)] mb-3">Coding Challenge</h2>
                                <p className="text-base text-[var(--text)] leading-relaxed overflow-y-auto">
                                    {currentPrompt}
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
                                isSubmitted={isSubmitted}
                            />
                        </div>
                    </div>

                    {/* Right Side - Expected Output, Score, and Controls (1/3 width) */}
                    <div className="right-panel flex flex-col col-span-1 h-full">
                        {/* Expected Console Output */}
                        <div className="expected-output-section mb-8 h-64"> {}
                            <h3 className="text-xl font-bold text-[var(--text)] mb-3">Expected Console Output</h3>
                            <div className="expected-output-content h-full">
                                <pre className="text-base text-[var(--text)] font-mono whitespace-pre-wrap p-4 bg-[var(--sliderhover)] rounded-lg border-2 border-[var(--accent)] strong-shadow h-full overflow-y-auto">
                                    {expectedOutput || 'No expected output available for this challenge.'}
                                </pre>
                            </div>
                        </div>

                        {/* Submission Result - Always visible, static size */}
                        <div className="result-section mt-6 h-40"> {}
                            <div className={`rounded-lg p-4 bg-[var(--sliderhover)] border-2 border-[var(--accent)] strong-shadow h-full flex flex-col`}>
                                <h3 className={`font-bold mb-2 text-[var(--text)]`}>Your Score</h3> {}
                                <div className="flex-1 flex flex-col justify-center">
                                    {isSubmitted && submissionResult ? (
                                        <>
                                            <p className="text-sm text-[var(--text)] mb-2">{submissionResult.message}</p>
                                            {submissionResult.details && (
                                                <div className="grid grid-cols-3 gap-2 text-sm text-[var(--text)]">
                                                    <div className="text-center">
                                                        <p className="font-bold text-xs">Structure</p>
                                                        <p className="text-base">{submissionResult.details.structureScore}%</p>
                                                    </div>
                                                    <div className="text-center">
                                                        <p className="font-bold text-xs">Output</p>
                                                        <p className="text-base">{submissionResult.details.outputScore}%</p>
                                                    </div>
                                                    <div className="text-center">
                                                        <p className="font-bold text-xs">Total</p>
                                                        <p className="text-base font-bold">{submissionResult.details.totalScore}%</p>
                                                    </div>
                                                </div>
                                            )}
                                        </>
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
                                disabled={!userCode.trim() || isSubmitted}
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
                                className="w-full px-6 py-3 bg-[var(--background)] border-2 border-[var(--accent)] text-[var(--accent)] font-bold rounded-lg transition-all duration-200 shadow-lg
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
