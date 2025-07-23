import React, { useEffect, useRef } from 'react';
import useLevelLogic from '../hooks/useLevelLogic';
import TypingArea from '../components/TypingArea';
import '../App.css';
import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';
import { createLevelSession } from '../services/apiService';
import { useLocation } from 'react-router-dom';

const Level = () => {
    const {
        codeSnippet,
        userInput,
        startTime,
        endTime,
        totalTyped,
        mistakes,
        speedLogRef,
        containerRef,
        handleKeyDown,
        focusContainer,
        speedLog,
        saveSession,
        calculateCPM,
        calculateAccuracy,
    } = useLevelLogic();

    const location = useLocation();
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
                saveSession();
            }

            const token = localStorage.getItem('loginToken');
            const accountId = localStorage.getItem('loginId');

            if (accountId != null) {
                try {
                    const sessionData = {
                        accountId: accountId,
                        levelId: location.state?.level.id,
                        cpm: calculateCPM(),
                        accuracy: calculateAccuracy(),
                        startTime: new Date(startTime).toISOString().slice(0, -1),
                        endTime: new Date(endTime).toISOString().slice(0, -1),
                    };
                    // const response = createLevelSession(sessionData, token);
                } catch (error) {
                    console.error('Error creating level session:', error);
                }
            }

            console.log('Navigating to /home');
            navigate('/home', { replace: true });
        }
    }, [endTime, navigate, speedLog, saveSession, calculateCPM, calculateAccuracy, startTime, location.state?.level.id]);

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