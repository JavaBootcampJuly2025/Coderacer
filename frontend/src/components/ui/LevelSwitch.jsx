import React from 'react';
import { useTheme } from '../../styles/ThemeContext';

const CONFIG = {
    cornerRadius: 12,
    panel: {
        width: '450px',
        height: '12',
        backgroundColor: 'var(--black)',
        borderRadius: '12px 12px 0 0',
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
    defaultOption: 'EASY',
};

const LevelSwitch = ({ selectedDifficulty, setSelectedDifficulty }) => {
    const { theme } = useTheme();

    const options = CONFIG.options;
    const selectedIndex = options.indexOf(selectedDifficulty);

    const sliderStyle = {
        transform: `translateX(${selectedIndex * parseInt(CONFIG.slider.width)}px)`,
        backgroundColor: CONFIG.slider.backgroundColor,
        transition: CONFIG.slider.transition,
        borderRadius: CONFIG.slider.borderRadius,
    };

    return (
        <div
            className="flex relative overflow-hidden"
            style={{
                width: CONFIG.panel.width,
                height: `${CONFIG.panel.height}px`,
                backgroundColor: CONFIG.panel.backgroundColor,
                borderRadius: CONFIG.panel.borderRadius,
            }}
        >
            <div
                className="absolute"
                style={{
                    width: CONFIG.slider.width,
                    height: `${CONFIG.slider.height}px`,
                    ...sliderStyle,
                }}
            />
            {options.map((option) => (
                <button
                    key={option}
                    onClick={() => setSelectedDifficulty(option)}
                    className="relative flex-1 h-full flex items-center justify-center"
                    style={{
                        color: CONFIG.button.textColor,
                        fontFamily: CONFIG.button.font,
                        fontSize: CONFIG.button.fontSize,
                        fontWeight: CONFIG.button.fontWeight,
                        zIndex: CONFIG.button.zIndex,
                    }}
                >
                    {selectedDifficulty !== option && (
                        <div
                            className="absolute inset-0 opacity-0 hover:opacity-100 transition-opacity duration-200"
                            style={{
                                backgroundColor: CONFIG.hoverOverlay.backgroundColor,
                                borderRadius: CONFIG.hoverOverlay.borderRadius,
                                width: CONFIG.slider.width,
                                height: `${CONFIG.panel.height}px`,
                            }}
                        />
                    )}
                    <span className="relative z-30 pointer-events-none">{option}</span>
                </button>
            ))}
        </div>
    );
};

export default LevelSwitch;
