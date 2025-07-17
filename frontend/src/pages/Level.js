import React from 'react';
import { useLocation } from 'react-router-dom';
import useTypingTest from '../hooks/useTypingTest';
import TypingArea from '../components/TypingArea';
import Results from '../components/Results';
import SpeedChart from '../components/SpeedChart';
import '../App.css';

// Default Lorem text for the code snippet
const Lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborummillus id est laborum.";

const Level = () => {
    const { state } = useLocation();
    const {
        codeSnippet,
        userInput,
        endTime,
        totalTyped,
        mistakes,
        speedLog,
        containerRef,
        handleKeyDown,
        calculateCPM,
        calculateAccuracy,
        focusContainer,
    } = useTypingTest(Lorem);

    return (
        <div
            style={{
                padding: '2rem',
                fontFamily: 'monospace',
                outline: 'none',
                minHeight: '100vh',
                backgroundColor: '#f9f9f9',
            }}
        >
            <h2>Typing Speed Test</h2>
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
            <SpeedChart endTime={endTime} speedLog={speedLog} />
        </div>
    );
};

export default Level;