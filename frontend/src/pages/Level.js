import React from 'react';
import useLevelLogic from '../hooks/useLevelLogic';
import TypingArea from '../components/TypingArea';
import Results from '../components/Results';
import SpeedChart from '../components/SpeedChart';
import '../App.css';

const Level = () => {
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
    } = useLevelLogic();

    return (
        <div className="home-wrapper min-h-screen bg-[#13223A] flex flex-col font-montserrat">
            <h2>Typing Speed Test</h2>
            <div className={"main-body content-center"}>
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
        </div>
    );
};

export default Level;