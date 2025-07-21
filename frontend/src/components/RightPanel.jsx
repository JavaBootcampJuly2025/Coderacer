import React from 'react';
import { useNavigate } from 'react-router-dom';
import LevelSwitch from './ui/LevelSwitch';
import LanguageGrid from './LanguageGrid';
import { useTheme } from '../styles/ThemeContext';

// Configuration constants
const PANEL_CONFIG = {
    panel: {
        width: '450px',
        height: '160px',
        borderRadius: '12px',
        background: 'var(--inbetween)',
        paddingX: '5',
    },
    buttons: {
        spacing: '5',
        default: {
            width: '60',
            height: '20',
            textSize: '35px',
            fontWeight: 'bold',
            font: 'montserrat',
            background: 'var(--primary-button)',
            hoverBackground: 'var(--primary-button-hover)',
            textColor: 'var(--text)',
            borderRadius: 'full',
            transitionDuration: '200ms',
        },
        // Individual button overrides (optional)
        variants: {
            Play: {},
            Challenges: {},
            Statistics: {},
        }
    }
};

const RightPanel = () => {
    const navigate = useNavigate();

    const handlePlayClick = () => navigate('/level?id=1');
    const handleThinkerClick = () => navigate('/GameMode');

    const buttons = [
        // { label: 'Challenges', onClick: handleChallengesClick },
        { label: 'Thinker', onClick: handleThinkerClick },
        { label: 'Speeder', onClick: handlePlayClick },
    ];

    return (
        <div
            className={`
                w-[${PANEL_CONFIG.panel.width}] 
                h-[${PANEL_CONFIG.panel.height}] 
                rounded-[${PANEL_CONFIG.panel.borderRadius}] 
                bg-[${PANEL_CONFIG.panel.background}] 
                flex flex-col
            `}
        >
            <LevelSwitch />
            <div className={`flex-grow flex flex-col justify-center space-y-${PANEL_CONFIG.buttons.spacing} px-${PANEL_CONFIG.panel.paddingX}`}>
                <div className={`flex justify-center space-x-${PANEL_CONFIG.buttons.spacing}`}>
                    {buttons.map(({ label, onClick }, index) => {
                        const buttonConfig = {
                            ...PANEL_CONFIG.buttons.default,
                            ...PANEL_CONFIG.buttons.variants[label]
                        };
                        return (
                            <button
                                key={index}
                                className={`
                                    w-${buttonConfig.width} 
                                    h-${buttonConfig.height} 
                                    bg-[${buttonConfig.background}] 
                                    rounded-${buttonConfig.borderRadius} 
                                    flex items-center justify-center 
                                    text-[${buttonConfig.textColor}] 
                                    text-${buttonConfig.textSize} 
                                    font-${buttonConfig.font} 
                                    font-${buttonConfig.fontWeight}
                                    cursor-pointer 
                                    hover:bg-[${buttonConfig.hoverBackground}] 
                                    transition-colors duration-${buttonConfig.transitionDuration}
                                `}
                                style={{ fontSize: buttonConfig.textSize }} // Apply textSize as fontSize
                                onClick={onClick}
                            >
                                {label}
                            </button>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

export default RightPanel;