import React, { useState } from 'react';
import { useTheme } from '../../styles/ThemeContext';

// Configuration object for the LevelSwitch component
const CONFIG = {
    cornerRadius: 12,
    panel: {
        width: '450px',
        height: '12',
        backgroundColor: 'var(--inbetween)',
        borderRadius: '12px 12px 0 0', // Rounded top corners, square bottom
        borderColor: 'var(--transparent)',
    },
    slider: {
        width: '150px',
        height: '12',
        backgroundColor: 'var(--primary-button)',
        borderRadius: '12px 12px 0 0',
        borderColor: 'var(--transparent)',
        transition: 'transform 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55)', // Fancier animation without scale
    },
    button: {
        textColor: 'var(--text)',
        font: 'montserrat',
        fontSize: 'sm',
        fontWeight: 'semibold',
        zIndex: '20',
    },
    hoverOverlay: {
        backgroundColor: 'var(--primary-button-hover)',
        borderRadius: '12px 12px 0 0',
        transition: 'opacity 0.2s ease-in-out',
    },
    options: ['Easy', 'Normal', 'Hard'],
    defaultOption: 'Normal',
};

const LevelSwitch = () => {
    const { theme } = useTheme();
    const [selected, setSelected] = useState(CONFIG.defaultOption);

    const options = CONFIG.options;
    console.log('Options:', options, 'Selected:', selected); // Debug log

    const selectedIndex = options.indexOf(selected);
    const sliderStyle = {
        transform: `translateX(${selectedIndex * parseInt(CONFIG.slider.width)}px)`,
        backgroundColor: CONFIG.slider.backgroundColor,
        transition: CONFIG.slider.transition,
        borderRadius: CONFIG.slider.borderRadius,
    };

    return (
        <div
            className={`flex w-[${CONFIG.panel.width}] h-${CONFIG.panel.height} bg-[${CONFIG.panel.backgroundColor}] overflow-hidden border border-[${CONFIG.panel.borderColor}]`}
            style={{ borderRadius: CONFIG.panel.borderRadius }} // Apply border radius directly
        >
            <div
                className={`absolute w-[${CONFIG.slider.width}] h-${CONFIG.slider.height}`}
                style={sliderStyle}
            />
            {options.map((option) => (
                <button
                    key={option}
                    onClick={() => setSelected(option)}
                    className={`relative flex-1 h-full flex items-center justify-center text-[${CONFIG.button.textColor}] font-${CONFIG.button.font} text-${CONFIG.button.fontSize} font-${CONFIG.button.fontWeight} z-${CONFIG.button.zIndex}`}
                >
                    <div
                        className={`absolute inset-0 opacity-0 hover:opacity-100 ${CONFIG.hoverOverlay.transition}`}
                        style={{
                            backgroundColor: CONFIG.hoverOverlay.backgroundColor,
                            borderRadius: CONFIG.hoverOverlay.borderRadius,
                        }}
                    />
                    <span className="relative z-30 pointer-events-none">{option}</span>
                </button>
            ))}
        </div>
    );
};

export default LevelSwitch;