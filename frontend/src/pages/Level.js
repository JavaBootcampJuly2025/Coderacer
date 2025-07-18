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
        speedLog,
        saveSession
    } = useLevelLogic();

    const navigate = useNavigate();
    const hasNavigated = useRef(false);

    useEffect(() => {
        console.log('--- useEffect triggered ---');
        console.log('endTime:', endTime);
        console.log('hasNavigated.current:', hasNavigated.current);
        console.log('speedLog:', speedLog);

        if (endTime && !hasNavigated.current) {
            console.log('Condition met - should navigate');
            hasNavigated.current = true;

            if (speedLog?.length > 1) {
                console.log('Saving session data...');
                saveSession(speedLog, endTime);
            }

            console.log('Navigating to /home');
            navigate('/home', { replace: true }); // Added replace to prevent back navigation
        }
    }, [endTime, navigate, speedLog, saveSession]);

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