import React, { useState } from 'react';
import { useTheme } from '../styles/ThemeContext';

// Configuration constants for the grid
const CONFIG = {
    // Grid layout
    GRID_COLUMNS: 3,
    GRID_WIDTH: 450,
    GRID_PADDING: 5,
    GRID_GAP: 0,
    GRID_BORDER_WIDTH: 0,

    // Button styling
    BUTTON_HEIGHT: 48,
    BUTTON_CORNER_RADIUS: 0,
    BUTTON_FONT_SIZE: 'sm',
    BUTTON_FONT_WEIGHT: 'semibold',

    // Animation
    PRESSED_SHADOW_TOP: 'inset 0 3px 6px rgba(0, 0, 0, 0.3)',
    PRESSED_SHADOW_BOTTOM: 'inset 0 -3px 6px rgba(0, 0, 0, 0.3)',
    PRESSED_OUTER_SHADOW: '0 2px 4px rgba(0, 0, 0, 0.2)',
    UNPRESSED_SHADOW: '0 2px 4px rgba(0, 0, 0, 0.1)',
    PRESSED_TRANSFORM: 'translateY(2px)',
    UNPRESSED_TRANSFORM: 'translateY(0)',
    TRANSITION_DURATION: '0.2s',
    SHADOW_OPACITY: 0.3,
};

const LanguageGrid = () => {
    const { theme } = useTheme();
    const [selectedLanguage, setSelectedLanguage] = useState(null);

    const languages = ['PYTHON', 'JAVASCRIPT', 'JAVA', 'C', 'CPP', 'CSHARP', 'GO', 'RUST', 'KOTLIN'];

    // Log theme and config for debugging
    console.log('Theme:', theme, 'CONFIG:', CONFIG);

    // Calculate button width with fallback and validation
    const buttonWidth =
        Number.isFinite(CONFIG.GRID_WIDTH) &&
        Number.isFinite(CONFIG.GRID_PADDING) &&
        Number.isFinite(CONFIG.GRID_GAP) &&
        CONFIG.GRID_COLUMNS > 0
            ? Math.max(
                0,
                (CONFIG.GRID_WIDTH - 2 * CONFIG.GRID_PADDING - (CONFIG.GRID_COLUMNS - 1) * CONFIG.GRID_GAP) /
                CONFIG.GRID_COLUMNS
            )
            : 100; // Default width if calculation fails
    console.log('Calculated buttonWidth:', buttonWidth);

    // Handle button click
    const handleSelect = (language) => {
        setSelectedLanguage(language === selectedLanguage ? null : language);
    };

    // Button styles with fallback
    const getButtonStyle = (isSelected) => {
        const shadowOpacity = Number.isFinite(CONFIG.SHADOW_OPACITY) ? CONFIG.SHADOW_OPACITY : 0.3;
        return {
            boxShadow: isSelected
                ? `${CONFIG.PRESSED_SHADOW_TOP}, ${CONFIG.PRESSED_SHADOW_BOTTOM}, ${CONFIG.PRESSED_OUTER_SHADOW.replace(
                    '0.2',
                    shadowOpacity
                )}`
                : CONFIG.UNPRESSED_SHADOW.replace('0.1', shadowOpacity),
            transform: isSelected ? CONFIG.PRESSED_TRANSFORM : CONFIG.UNPRESSED_TRANSFORM,
            transition: `box-shadow ${CONFIG.TRANSITION_DURATION} ease-in-out, transform ${CONFIG.TRANSITION_DURATION} ease-in-out`,
            borderRadius: `${CONFIG.BUTTON_CORNER_RADIUS}px`,
        };
    };

    return (
        <div
            className={`grid grid-cols-${CONFIG.GRID_COLUMNS} gap-${CONFIG.GRID_GAP / 4} p-${CONFIG.GRID_PADDING / 4} bg-[var(--background)] rounded-[${CONFIG.BUTTON_CORNER_RADIUS}px] border-[${CONFIG.GRID_BORDER_WIDTH}px] border-[var(--border-gray)]`}
            style={{ width: CONFIG.GRID_WIDTH ? `${CONFIG.GRID_WIDTH}px` : '450px' }} // Fallback width
        >
            {languages &&
                languages.map((language) => (
                    <button
                        key={language}
                        onClick={() => handleSelect(language)}
                        className={`h-${CONFIG.BUTTON_HEIGHT / 4} bg-[var(--background)] flex items-center justify-center text-[var(--text)] text-${CONFIG.BUTTON_FONT_SIZE} font-${CONFIG.BUTTON_FONT_WEIGHT} cursor-pointer hover:bg-[var(--primary-button-hover)] transition-colors duration-200`}
                        style={{
                            ...getButtonStyle(selectedLanguage === language),
                            width: `${buttonWidth}px`, // Apply calculated width
                        }}
                    >
                        {language || 'Unknown'} {/* Fallback for undefined language */}
                    </button>
                ))}
        </div>
    );
};

export default LanguageGrid;