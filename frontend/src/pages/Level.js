import React, { useEffect, useRef } from 'react';
import useLevelLogic from '../hooks/useLevelLogic';
import TypingArea from '../components/TypingArea';
import '../App.css';
import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';

const Level = () => {
    const {
        codeSnippet,
        userInput,
        endTime,
        containerRef,
        handleKeyDown,
        focusContainer,
        speedLog,          // Add this
        saveSession
    } = useLevelLogic();

    const navigate = useNavigate();
    const hasNavigated = useRef(false); // Prevent multiple navigations

// In Level.js
    useEffect(() => {
        console.log('useEffect triggered, endTime:', endTime);
        if (endTime && !hasNavigated.current) {
            hasNavigated.current = true;
            if (speedLog && speedLog.length > 1) {
                saveSession(speedLog, endTime);
            }
            console.log('Navigating to /home');
            navigate('/home');
        }
    }, [endTime, navigate, saveSession, speedLog]);

    return (
        <div className="home-wrapper min-h-screen bg-[#13223A] flex flex-col font-montserrat">
            <Header />
            <div className="main-body content-center">
                <TypingArea
                    codeSnippet={codeSnippet}
                    userInput={userInput}
                    containerRef={containerRef}
                    handleKeyDown={handleKeyDown}
                    focusContainer={focusContainer}
                />
            </div>
        </div>
    );
};

export default Level;