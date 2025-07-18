// src/styles/ThemeContext.js
import React, { createContext, useContext, useState } from 'react';
import themes from './colors';

const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
    const [theme, setTheme] = useState('light');

    const applyTheme = (themeName) => {
        const root = document.documentElement;
        const selectedTheme = themes[themeName];
        Object.keys(selectedTheme).forEach((key) => {
            root.style.setProperty(`--${key.replace(/([A-Z])/g, '-$1').toLowerCase()}`, selectedTheme[key]);
        });
        setTheme(themeName);
    };

    return (
        <ThemeContext.Provider value={{ theme, applyTheme, themes }}>
            {children}
        </ThemeContext.Provider>
    );
};

export const useTheme = () => useContext(ThemeContext);