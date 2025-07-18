import React, { createContext, useContext, useState } from 'react';

const LevelContext = createContext(undefined, undefined);

export const LevelProvider = ({ children }) => {
    const [latestSession, setLatestSession] = useState(null);

    const saveSession = (speedLog, endTime) => {
        const session = { speedLog, endTime, timestamp: new Date().toISOString() };
        console.log('Saving to LevelContext:', session);
        setLatestSession(session);
    };

    return <LevelContext.Provider value={{ latestSession, saveSession }}>{children}</LevelContext.Provider>;
};

export const useLevelContext = () => useContext(LevelContext);