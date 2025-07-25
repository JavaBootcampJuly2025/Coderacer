import React, { createContext, useContext, useState } from 'react';

const LevelContext = createContext(undefined, undefined);

export const LevelProvider = ({ children }) => {
    const [latestSession, setLatestSession] = useState(null);

    const saveSession = (speedLog, endTime, totalTyped, mistakes, userInput, codeSnippet) => {
        setLatestSession({
            speedLog,
            endTime,
            totalTyped,
            mistakes,
            userInput,
            codeSnippet,
            timestamp: new Date().toISOString(),
        });
        const accountId = localStorage.getItem('loginId');
    };

    return <LevelContext.Provider value={{ latestSession, saveSession }}>{children}</LevelContext.Provider>;
};

export const useLevelContext = () => useContext(LevelContext);