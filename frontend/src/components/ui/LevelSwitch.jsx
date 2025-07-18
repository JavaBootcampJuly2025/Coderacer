import React, { useState } from 'react';
import { useTheme } from '../../styles/ThemeContext';

// Constant for corner radius (in pixels)
const CORNER_RADIUS = 12;

const LevelSwitch = () => {
    const { theme } = useTheme(); // Access the current theme
    const [selected, setSelected] = useState('Normal'); // Default to Normal

    // Map of options to their indices for sliding animation
    const options = ['Easy', 'Normal', 'Hard'];
    const selectedIndex = options.indexOf(selected);

    // Calculate the translateX value for the slider (each button is 150px wide)
    const sliderStyle = {
        transform: `translateX(${selectedIndex * 150}px)`,
        backgroundColor: `var(--primary-button)`,
        transition: 'transform 0.3s ease-in-out',
        borderRadius: `${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0`, // Round top corners only
    };

    return (
        <div
            className={`flex w-[450px] h-12 bg-[var(--background)] rounded-[${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0] overflow-hidden border-transparent`}
        >
            <div
                className="absolute w-[150px] h-12"
                style={sliderStyle}
            />
            {options.map((option) => (
                <button
                    key={option}
                    onClick={() => setSelected(option)}
                    className="relative flex-1 h-full flex items-center justify-center text-[var(--text)] font-montserrat text-sm font-semibold z-20"
                >
                    {selected !== option && (
                        <div
                            className="absolute inset-0 transition-opacity duration-200 hover:opacity-100 opacity-0"
                            style={{
                                backgroundColor: `var(--primary-button-hover)`,
                                borderRadius: `${CORNER_RADIUS}px ${CORNER_RADIUS}px 0 0`, // Match container's top-only rounding
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