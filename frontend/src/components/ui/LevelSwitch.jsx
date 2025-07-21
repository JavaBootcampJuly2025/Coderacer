import React, { useState } from 'react';
import { useTheme } from '../../styles/ThemeContext';

// Constant for corner radius (in pixels)
const CORNER_RADIUS = 12;

const LevelSwitch = ({ selectedDifficulty, setSelectedDifficulty }) => {
    const { theme } = useTheme();

    const options = ['EASY', 'MEDIUM', 'HARD'];
    console.log('Options:', options, 'Selected:', selectedDifficulty); // Debug log

    const selectedIndex = options.indexOf(selectedDifficulty);
    const sliderStyle = {
        transform: `translateX(${selectedIndex * 150}px)`,
        backgroundColor: `var(--primary-button)`,
        transition: 'transform 0.3s ease-in-out',
        borderRadius: `${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0`,
    };

    return (
        <div
            className={`flex w-[450px] h-12 bg-[var(--background)] rounded-[${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0] overflow-hidden border border-[var(--border-gray)]`}
        >
            <div
                className="absolute w-[150px] h-12"
                style={sliderStyle}
            />
            {options.map((option) => (
                <button
                    key={option}
                    onClick={() => setSelectedDifficulty(option)}
                    className="relative flex-1 h-full flex items-center justify-center text-[var(--text)] font-montserrat text-sm font-semibold z-20"
                >
                    {selectedDifficulty !== option && (
                        <div
                            className="absolute inset-0 transition-opacity duration-200 hover:opacity-100 opacity-0"
                            style={{
                                backgroundColor: `var(--primary-button-hover)`,
                                borderRadius: `${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0`,
                            }}
                        />
                    )}
                    <span className="relative z-30">{option}</span>
                </button>
            ))}
        </div>
    );
};

export default LevelSwitch;