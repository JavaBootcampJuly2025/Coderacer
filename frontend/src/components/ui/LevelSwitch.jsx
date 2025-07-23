import React, { useState } from 'react';
import { useTheme } from '../../styles/ThemeContext';

// Configuration object for the LevelSwitch component
const CONFIG = {
    cornerRadius: 12,
    panel: {
        width: '450px',
        height: '12',
        backgroundColor: 'var(--background)',
        borderRadius: '12px 12px 0 0', // Rounded top corners, square bottom
    },
    slider: {
        width: '150px',
        height: '12',
        backgroundColor: 'var(--inbetween)',
        borderRadius: '12px 12px 0 0',
        borderColor: 'var(--primary-button)',
        transition: 'transform 0.1s ease-in-out',
    },
    button: {
        textColor: 'var(--text)',
        font: 'montserrat',
        fontSize: 'sm',
        fontWeight: 'semibold',
        zIndex: '20',
    },
    hoverOverlay: {
        backgroundColor: 'var(--sliderhover)',
        borderRadius: '12px 12px 0 0',
        transition: 'opacity 0.2s ease-in-out',
    },
    options: ['EASY', 'MEDIUM', 'HARD'],
    defaultOption: 'MEDIUM',
};

const LevelSwitch = ({ selectedDifficulty, setSelectedDifficulty }) => {
    const { theme } = useTheme();


    const options = CONFIG.options;
    console.log('Options:', options, 'Selected:', selectedDifficulty); // Debug log

    const selectedIndex = options.indexOf(selectedDifficulty);
    const sliderStyle = {
        transform: `translateX(${selectedIndex * parseInt(CONFIG.slider.width)}px) `,
        backgroundColor: CONFIG.slider.backgroundColor,
        transition: CONFIG.slider.transition,
        borderRadius: CONFIG.slider.borderRadius,
    };


    return (
        <div
            className={`flex w-[${CONFIG.panel.width}] h-${CONFIG.panel.height} bg-[${CONFIG.panel.backgroundColor}] overflow-hidden border-l border-r border-t border-[var(--border-gray)] rounded-b-2xl`}
            style={{ borderRadius: CONFIG.panel.borderRadius }}
        >
            <div
                className={`absolute w-[${CONFIG.slider.width}] h-${CONFIG.slider.height}`}
                style={sliderStyle}
            />
            {options.map((option) => (
                <button
                    key={option}
                    onClick={() => setSelectedDifficulty(option)}
                    className={`level-switch-button relative flex-1 h-full flex items-center justify-center text-[${CONFIG.button.textColor}] font-${CONFIG.button.font} text-${CONFIG.button.fontSize} font-${CONFIG.button.fontWeight} z-${CONFIG.button.zIndex}`}
                >
                    <div
                        className={`absolute inset-0 opacity-0 hover:opacity-100 ${CONFIG.hoverOverlay.transition} ${option === selectedDifficulty ? 'pointer-events-none' : ''}`}
                        style={{
                            backgroundColor: CONFIG.hoverOverlay.backgroundColor,
                            borderRadius: CONFIG.hoverOverlay.borderRadius,
                            width: CONFIG.slider.width,
                            height: CONFIG.panel.height,
                        }}
                    />
                    <span className="relative z-30 pointer-events-none">{option}</span>
                </button>
            ))}
        </div>
    );
};

export default LevelSwitch;