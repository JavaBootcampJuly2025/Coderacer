import React from 'react';
import styles from './TypingArea.module.css';
import colors from '../styles/colors'; // Import colors
import { useTheme } from '../styles/ThemeContext'

const TypingArea = ({ codeSnippet, userInput, containerRef, handleKeyDown, focusContainer, words, currentWordIndex, currentWordInput, wordStates }) => {
    const { theme } = useTheme();

    // If word-based props are not available, fall back to original character-based rendering
    if (!words || !wordStates) {
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
    }

    // Word-based rendering
    return (
        <div
            tabIndex={0}
            ref={containerRef}
            onKeyDown={handleKeyDown}
            onClick={focusContainer}
            className={styles.container}
        >
            {words.map((word, wordIdx) => {
                const wordState = wordStates[wordIdx] || { status: 'untyped', userText: '', hasError: false };
                const isCurrentWord = wordIdx === currentWordIndex;
                const userText = isCurrentWord ? currentWordInput : wordState.userText;

                // Handle whitespace words (spaces, newlines, etc.)
                if (/^\s+$/.test(word)) {
                    return (
                        <span key={wordIdx} className={styles.whitespace}>
                            {word}
                        </span>
                    );
                }

                // Determine word styling
                let wordClassName = styles.word;
                if (wordState.hasError || wordState.status === 'incorrect') {
                    wordClassName += ` ${styles.incorrectWord}`;
                }

                return (
                    <span key={wordIdx} className={wordClassName}>
                        {/* Show cursor at the beginning of the word if no characters typed yet and this is current word */}
                        {isCurrentWord && userText.length === 0 && (
                            <span className={styles.caret}></span>
                        )}

                        {word.split('').map((char, charIdx) => {
                            let color = colors[theme].unwrittenChar; // Default untyped color
                            let backgroundColor = 'transparent';

                            if (charIdx < userText.length) {
                                const userChar = userText[charIdx];
                                if (userChar === char) {
                                    color = colors[theme].correctChar; // Blue for correct
                                } else {
                                    color = colors[theme].incorrectChar; // Red for incorrect
                                }
                            }

                            return (
                                <React.Fragment key={charIdx}>
                                    <span
                                        className={styles.char}
                                        style={{ color, backgroundColor }}
                                    >
                                        {char === ' ' ? '\u00A0' : char}
                                    </span>
                                    {/* Show cursor after this character if it's the last typed character in current word */}
                                    {isCurrentWord && charIdx === userText.length - 1 && userText.length > 0 && userText.length <= word.length && (
                                        <span className={styles.caret}></span>
                                    )}
                                </React.Fragment>
                            );
                        })}

                        {/* Show extra characters in light red */}
                        {userText.length > word.length && (
                            <span className={styles.extraChars}>
                                {userText.slice(word.length).split('').map((extraChar, extraIdx) => (
                                    <React.Fragment key={`extra-${extraIdx}`}>
                                        <span
                                            className={styles.char}
                                            style={{
                                                color: colors[theme].extraText,
                                                backgroundColor: 'transparent' // Set to transparent or another theme variable if desired
                                            }}
                                        >
                                            {extraChar === ' ' ? '\u00A0' : extraChar}
                                        </span>
                                        {/* Show cursor after the last extra character */}
                                        {isCurrentWord && extraIdx === userText.slice(word.length).length - 1 && (
                                            <span className={styles.caret}></span>
                                        )}
                                    </React.Fragment>
                                ))}
                            </span>
                        )}
                    </span>
                );
            })}
        </div>
    );
};

export default TypingArea;
