import React from 'react';
import styles from './TypingArea.module.css';
import colors from '../styles/colors'; // Import colors
import { useTheme } from '../styles/ThemeContext'

const TypingArea = ({ codeSnippet, userInput, containerRef, handleKeyDown, focusContainer }) => {
    const { theme } = useTheme();
    return (
        <div
            tabIndex={0}
            ref={containerRef}
            onKeyDown={handleKeyDown}
            onClick={focusContainer}
            className={styles.container}
        >
            {codeSnippet.split('').map((char, idx) => {
                let color = colors[theme].unwrittenChar; // Default to unwritten text color
                if (idx < userInput.length) {
                    color = userInput[idx] === char ? colors[theme].correctChar : colors[theme].incorrectChar;
                }

                // Replace special characters for display
                const displayChar = char === ' ' ? ' ' : char === ' ' ? '\u00A0' : char;

                return (
                    <React.Fragment key={idx}>
                        <span className={styles.char} style={{ color }}>
                            {displayChar}
                        </span>
                        {idx === userInput.length - 1 && userInput.length > 0 && userInput.length <= codeSnippet.length && (
                            <span className={styles.caret}> </span>
                        )}
                    </React.Fragment>
                );
            })}
        </div>
    );
};

export default TypingArea;