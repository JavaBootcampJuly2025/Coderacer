import React, { useEffect } from 'react';
import useLevelLogic from '../hooks/useLevelLogic';
import TypingArea from '../components/TypingArea';
import Results from '../components/Results';
import SpeedChart from '../components/SpeedChart';
import '../App.css';
import Header from '../components/Header';
import { useNavigate } from 'react-router-dom';

const Level = () => {
    const {
        codeSnippet,
        userInput,
        endTime,
        totalTyped,
        mistakes,
        speedLogRef,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useLevelLogic();

    const navigate = useNavigate();

    // Redirect to home when endTime is set
    useEffect(() => {
        console.log('endTime updated:', endTime); // Debug log
        if (endTime) {
            navigate('/home'); // Immediate redirect
        }
    }, [endTime, navigate]); // Trigger only on endTime change

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
                <Results
                    endTime={endTime}
                    calculateCPM={calculateCPM}
                    calculateAccuracy={calculateAccuracy}
                    totalTyped={totalTyped}
                    mistakes={mistakes}
                />
            <SpeedChart endTime={endTime} speedLog={speedLogRef.current} />
            </div>
        </div>
    );
};

export default Level;