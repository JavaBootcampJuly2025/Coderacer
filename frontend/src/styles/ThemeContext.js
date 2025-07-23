import React, { createContext, useContext, useState, useEffect } from 'react';
import themes from './colors';

const ThemeContext = createContext();

export const ThemeProvider = ({ children }) => {
    // Initialize theme from localStorage or default to 'dark'
    const [theme, setTheme] = useState(() => {
        const savedTheme = localStorage.getItem('theme');
        return savedTheme && themes[savedTheme] ? savedTheme : 'dark';
    });

    // Apply theme by setting CSS variables
    const applyTheme = (themeName) => {
        const root = document.documentElement;
        const selectedTheme = themes[themeName];
        Object.keys(selectedTheme).forEach((key) => {
            root.style.setProperty(`--${key.replace(/([A-Z])/g, '-$1').toLowerCase()}`, selectedTheme[key]);
        });
        setTheme(themeName);
        // Save theme to localStorage
        localStorage.setItem('theme', themeName);
    };

    // Apply theme on initial render
    useEffect(() => {
        applyTheme(theme);
    }, []); // Empty dependency array ensures this runs once on mount

    return (
        <ThemeContext.Provider value={{ theme, applyTheme, themes }}>
            {children}
        </ThemeContext.Provider>
    );
};

export const useTheme = () => useContext(ThemeContext);