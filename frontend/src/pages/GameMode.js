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
        <div className="home-wrapper min-h-screen bg-[#13223A] flex flex-col font-montserrat">
            <Header />
            <div className="main-body flex-1 flex flex-col p-6">
                {/* Prompt Display Area */}
                <div className="prompt-section mb-6">
                    <div className="bg-[#1E3A5F] rounded-lg p-6 shadow-lg">
                        <h2 className="text-xl font-semibold text-white mb-4">Coding Challenge</h2>
                        <p className="text-gray-200 text-lg leading-relaxed">
                            {currentPrompt}
                        </p>
                    </div>
                </div>

                {/* Code Input Area */}
                <div className="code-input-section flex-1 mb-6">
                    <CodeInputArea
                        userCode={userCode}
                        containerRef={containerRef}
                        handleCodeChange={handleCodeChange}
                        focusContainer={focusContainer}
                        isSubmitted={isSubmitted}
                    />
                </div>

                {/* Submission Result */}
                {isSubmitted && submissionResult && (
                    <div className="result-section mb-6">
                        <div className={`rounded-lg p-4 ${
                            submissionResult.success
                                ? 'bg-green-900/30 border border-green-500'
                                : 'bg-red-900/30 border border-red-500'
                        }`}>
                            <h3 className={`font-semibold mb-2 ${
                                submissionResult.success ? 'text-green-400' : 'text-red-400'
                            }`}>
                                {submissionResult.success ? 'Success!' : 'Try Again'}
                            </h3>
                            <p className="text-gray-200">{submissionResult.message}</p>
                        </div>
                    </div>
                )}

                {/* Action Buttons */}
                <div className="action-buttons flex gap-4 justify-center">
                    <button
                        onClick={handleSubmit}
                        disabled={!userCode.trim() || isSubmitted}
                        className="px-6 py-3 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition-colors duration-200"
                    >
                        Submit Answer
                    </button>

                    <button
                        onClick={generateNewPrompt}
                        className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white font-semibold rounded-lg transition-colors duration-200"
                    >
                        New Challenge
                    </button>

                </div>
            </div>
        </div>
    );
};

export default GameMode;
