import React from 'react';
import Settings from '../assets/settings.png';
import { useTheme } from '../styles/ThemeContext';
import Title from './ui/Title';
import Icon from '../assets/icon.svg?react';
import { ReactComponent as Logo } from '../assets/icon.svg';
import { ReactComponent as SunIcon } from '../assets/sun.svg';
import { ReactComponent as MoonIcon } from '../assets/moon.svg';
import { ReactComponent as UserIcon } from '../assets/user.svg';
import { ReactComponent as SettingsIcon } from '../assets/settings.svg';

const Header = () => {
    const { theme, applyTheme } = useTheme(); // Access theme and applyTheme

    // Toggle between light and dark themes
    const toggleTheme = () => {
        applyTheme(theme === 'light' ? 'dark' : 'light');
    };

    return (
        <div className="w-full h-24 flex justify-between items-center px-5">
            <div className="w-64 flex flex-row items-center space-x-3">
                <Logo className="logo" alt="Coderacer logo" />
                <Title />
            </div>
            <div className="flex justify-center space-x-5">
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                    onClick={toggleTheme}
                    title={`Switch to ${theme === 'light' ? 'dark' : 'light'} theme`}
                >
                    <span className="text-[var(--text)] text-2xl font-montserrat">
                        {theme === 'light' ? <SunIcon className="theme-icon" /> : <MoonIcon className="theme-icon" />}
                    </span>
                </button>
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                >
                    <UserIcon className="theme-icon" alt="Profile"/>
                </button>
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                >
                    <SettingsIcon className="theme-icon" alt="Settings"/>
                </button>
            </div>
        </div>
    );
};

export default Header;