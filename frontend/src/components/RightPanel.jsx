import React from 'react';
import { useNavigate } from 'react-router-dom';
import LevelSwitch from './ui/LevelSwitch';
import LanguageGrid from './LanguageGrid';
import { useTheme } from '../styles/ThemeContext';

// Configuration object for the RightPanel component
const CONFIG = {
    panel: {
        width: '450px',
        height: '200px',
        borderRadius: '10px',
        backgroundColor: 'var(--leaderboard-bg)',
        flexDirection: 'column',
        className: 'flex',
    },
    container: {
        paddingX: '5',
        spacingY: '5',
        className: 'flex-grow flex flex-col justify-center',
    },
    buttonContainer: {
        spacingX: '5',
        className: 'flex justify-center',
    },
    buttons: [
        {
            label: 'Challenges',
            path: '/challenges',
            width: '24',
            height: '12',
            backgroundColor: 'var(--primary-button)',
            hoverBackgroundColor: 'var(--primary-button-hover)',
            textColor: 'var(--text)',
            font: 'montserrat',
            fontSize: 'sm',
            borderRadius: 'full',
            transition: 'colors duration-200',
            className: 'flex items-center justify-center cursor-pointer',
        },
        {
            label: 'Statistics',
            path: '/statistics',
            width: '24',
            height: '12',
            backgroundColor: 'var(--primary-button)',
            hoverBackgroundColor: 'var(--primary-button-hover)',
            textColor: 'var(--text)',
            font: 'montserrat',
            fontSize: 'sm',
            borderRadius: 'full',
            transition: 'colors duration-200',
            className: 'flex items-center justify-center cursor-pointer',
        },
        {
            label: 'Play',
            path: '/level?id=1',
            width: '24',
            height: '12',
            backgroundColor: 'var(--primary-button)',
            hoverBackgroundColor: 'var(--primary-button-hover)',
            textColor: 'var(--text)',
            font: 'montserrat',
            fontSize: 'sm',
            borderRadius: 'full',
            transition: 'colors duration-200',
            className: 'flex items-center justify-center cursor-pointer',
        },
    ],
};

const RightPanel = () => {
    const navigate = useNavigate();

    const handleNavigation = (path) => () => navigate(path);

    return (
        <div
            className={`w-${CONFIG.panel.width} h-${CONFIG.panel.height} rounded-${CONFIG.panel.borderRadius} bg-${CONFIG.panel.backgroundColor} ${CONFIG.panel.className} flex-column`}
        >
            <LevelSwitch />
            {/*<LanguageGrid />*/}
            <div className={`${CONFIG.container.className} space-y-${CONFIG.container.spacingY} px-${CONFIG.container.paddingX}`}>
                <div className={`${CONFIG.buttonContainer.className} space-x-${CONFIG.buttonContainer.spacingX}`}>
                    {CONFIG.buttons.map((button, index) => (
                        <button
                            key={index}
                            className={`w-${button.width} h-${button.height} bg-${button.backgroundColor} rounded-${button.borderRadius} text-${button.textColor} text-${button.fontSize} font-${button.font} hover:bg-${button.hoverBackgroundColor} transition-${button.transition} ${button.className}`}
                            onClick={handleNavigation(button.path)}
                        >
                            {button.label}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default RightPanel;