import React, { useState } from 'react';
import { useTheme } from '../styles/ThemeContext';
import useAccountInfo from '../hooks/useAccountInfo';
import Title from './ui/Title';
import { ReactComponent as Logo } from '../assets/icon.svg';
import { ReactComponent as SunIcon } from '../assets/sun.svg';
import { ReactComponent as MoonIcon } from '../assets/moon.svg';
import { ReactComponent as UserIcon } from '../assets/user.svg';
import { ReactComponent as SettingsIcon } from '../assets/settings.svg';
import { Link } from 'react-router-dom';
import ProfilePopup from './ProfilePopup';
import SettingsPopup from './SettingsPopup';

const Header = () => {
    const { theme, applyTheme } = useTheme();
    const { username, email, rating, avgCpm, avgAccuracy, loggedOn, updateAccountInfo } = useAccountInfo();
    const [selectedPopup, setSelectedPopup] = useState(null);

    const selectPopup = (newPopup) => {
        if(selectedPopup === newPopup) setSelectedPopup(null);
        else setSelectedPopup(newPopup);
    };

    const toggleTheme = () => {
        const themes = ['light', 'dark', 'brown', 'mentor'];
        const currentIndex = themes.indexOf(theme);
        const nextIndex = (currentIndex + 1) % themes.length;
        applyTheme(themes[nextIndex]);
    };

    const getNextThemeLabel = () => {
        const themes = ['light', 'dark', 'brown', 'mentor'];
        const currentIndex = themes.indexOf(theme);
        const nextIndex = (currentIndex + 1) % themes.length;
        return themes[nextIndex];
    };

    return (
        <div className="w-full h-24 flex justify-between items-center px-5">
            <Link to="/home" className="w-64 h-20 flex flex-row items-center space-x-3 hover:opacity-80 transition">
                <Logo className="logo" alt="Coderacer logo" />
                <Title />
            </Link>
            <div className="flex justify-center space-x-5">
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                    onClick={toggleTheme}
                    title={`Switch to ${getNextThemeLabel()} theme`}
                >
                    <span className="text-[var(--text)] text-2xl font-montserrat">
    {theme === 'light' ? (
        <SunIcon className="theme-icon" />
    ) : theme === 'dark' ? (
        <MoonIcon className="theme-icon" />
    ) : theme === 'brown' ? (
        <span className="theme-icon">💼</span>
    ) : theme === 'mentor' ? (
        <span className="theme-icon">🔵</span>
    ) : (
        <SunIcon className="theme-icon" />
    )}
  </span>
                </button>
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                    onClick={() => selectPopup("Profile")}
                    title="User Info"
                >
                    <UserIcon className="theme-icon" alt="Profile" />
                </button>

                {(selectedPopup === "Profile") && (
                    <ProfilePopup username={username} rating={rating} email={email} avgCpm={avgCpm}
                                  avgAccuracy={avgAccuracy} loggedOn={loggedOn} updateAccountInfo={updateAccountInfo} />
                )}
                <button
                    className="round-button w-12 h-12 bg-[var(--primary-button)] rounded-full hover:bg-[var(--primary-button-hover)] transition flex items-center justify-center p-0"
                    onClick={() => selectPopup("Settings")}
                    title="Settings"
                >
                    <SettingsIcon className="theme-icon" alt="Settings"/>
                </button>

                {(selectedPopup === "Settings") && (
                    <SettingsPopup loggedOn={loggedOn} updateAccountInfo={updateAccountInfo} />
                )}
            </div>
        </div>
    );
};

export default Header;