// src/components/ThemeSwitcher.jsx
import React from 'react';
import { useTheme } from '../styles/ThemeContext';

const ThemeSwitcher = () => {
    const { applyTheme } = useTheme();

    return (
        <div>
            <button onClick={() => applyTheme('light')}>Light Theme</button>
            <button onClick={() => applyTheme('dark')}>Dark Theme</button>
        </div>
    );
};

export default ThemeSwitcher;