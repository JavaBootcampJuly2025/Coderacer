import React from 'react';
import {getRandomLevelWithDifficulty, getRandomLevelWithParameters} from '../services/apiService';
import { useNavigate } from 'react-router-dom';
import LevelSwitch from './ui/LevelSwitch';
import LanguageGrid from './LanguageGrid';
import { useState } from 'react';
import { useTheme } from '../styles/ThemeContext';

// Configuration constants
const PANEL_CONFIG = {
    panel: {
        width: '452px',
        height: '300px',
        borderRadius: '15px',
        background: 'var(--inbetween)',
        paddingX: '5',
    },
    buttons: {
        spacing: '5',
        default: {
            width: '410px',
            height: '20',
            textSize: '26px',
            fontWeight: 'bold',
            font: 'montserrat',
            background: 'var(--primary-button)',
            hoverBackground: 'var(--primary-button-hover)',
            textColor: 'var(--text)',
            borderRadius: 'full',
            transitionDuration: '200ms',
            letterSpacing: '0.15em',
        },
    }
};

const RightPanel = () => {
    const [selectedDifficulty, setSelectedDifficulty] = useState('MEDIUM');

    const navigate = useNavigate();

    const handlePlayClick = async () => {
        try {
            const levelData = await getRandomLevelWithDifficulty(selectedDifficulty);
            navigate("/level", { state: { level: levelData } });
        } catch (error) {
            console.error('Error loading level:', error);
        }
    };

    const handleThinkerClick = () => navigate('/GameMode');

    const buttons = [
        { label: 'THINKER MODE', onClick: handleThinkerClick },
        { label: 'SPEEDER MODE', onClick: handlePlayClick },
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
            style={{
                boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
            }}
        >
            <LevelSwitch
                selectedDifficulty={selectedDifficulty}
                setSelectedDifficulty={setSelectedDifficulty}
            />
            <div className={`flex-grow flex border-l border-r border-b border-[var(--border-gray)] rounded-b-2xl shadow-lg flex-col justify-center space-y-${PANEL_CONFIG.buttons.spacing} px-${PANEL_CONFIG.panel.paddingX}`}>
                <div className={`flex-col justify-center space-y-${PANEL_CONFIG.buttons.spacing}`}>
                    {buttons.map(({ label, onClick }, index) => {
                        const buttonConfig = PANEL_CONFIG.buttons.default;
                        return (
                            <button
                                key={index}
                                className={`
                                    w-[${buttonConfig.width}]
                                    h-${buttonConfig.height} 
                                    bg-[${buttonConfig.background}] 
                                    rounded-${buttonConfig.borderRadius} 
                                    flex items-center justify-center 
                                    text-[${buttonConfig.textColor}] 
                                    text-${buttonConfig.textSize} 
                                    font-${buttonConfig.font} 
                                    font-${buttonConfig.fontWeight}
                                    tracking-[${buttonConfig.letterSpacing}] 
                                    cursor-pointer 
                                    hover:bg-[${buttonConfig.hoverBackground}] 
                                    transition-all duration-${buttonConfig.transitionDuration}
                                    hover:shadow-lg
                                    hover:scale-[1.02]
                                `}
                                style={{
                                    fontSize: buttonConfig.textSize,
                                    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
                                }}
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
