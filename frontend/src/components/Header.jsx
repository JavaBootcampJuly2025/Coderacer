import React from 'react';
import Settings from '../assets/settings.png';
import { useTheme } from '../styles/ThemeContext';
import useAccountInfo from '../hooks/useAccountInfo';
import Title from './ui/Title';
import Icon from '../assets/icon.svg?react';

const Header = () => {
    const { theme, applyTheme } = useTheme(); // Access theme and applyTheme
    const { username, email, rating } = useAccountInfo(); // Access account info if logged in

    // Toggle between light and dark themes
    const toggleTheme = () => {
        applyTheme(theme === 'light' ? 'dark' : 'light');
    };

    return (
        <div className="w-full h-24 flex justify-between items-center px-5">
            <text style={{color: "white"}}>{username}</text>
            <text style={{color: "white"}}>{rating}</text>
            <div className="w-64 flex flex-row items-center space-x-3">
                {/*<Icon className="w-14 h-14" style={{ fill: 'var(--accent)' }} alt="Codegobrr Icon" />*/}
                <Title />
            </div>
            <div className="flex justify-center space-x-5">
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                    onClick={toggleTheme}
                    title={`Switch to ${theme === 'light' ? 'dark' : 'light'} theme`}
                >
                    <span className="text-[var(--text)] text-2xl font-montserrat">
                        {theme === 'light' ? '☀️' : '🌙'}
                    </span>
                </button>
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                >
                    <img
                        src={Icon}
                        className="w-10 h-10 rounded-full object-cover"
                        alt="Profile"
                    />
                </button>
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                >
                    <img
                        src={Settings}
                        className="w-10 h-10 rounded-full object-cover"
                        alt="Settings"
                    />
                </button>
            </div>
        </div>
    );
};

export default Header;