import React from 'react';

const TypingArea = ({ codeSnippet, userInput, containerRef, handleKeyDown, focusContainer }) => {
    return (
        <div
            tabIndex={0}
            ref={containerRef}
            onKeyDown={handleKeyDown}
            onClick={focusContainer}
            style={{
                padding: '1rem',
                marginTop: '1rem',
                background: '#fff',
                border: '2px solid #ccc',
                borderRadius: '8px',
                whiteSpace: 'pre-wrap',
                wordWrap: 'break-word',
                fontSize: '16px',
                lineHeight: '1.5',
                minHeight: '200px',
                cursor: 'text',
            }}
        >
            {codeSnippet.split('').map((char, idx) => {
                let color = '#888';
                if (idx < userInput.length) {
                    color = userInput[idx] === char ? 'green' : 'red';
                }
                return (
                    <span key={idx} style={{ color }}>
                        {char}
                    </span>
                );
            })}
            {userInput.length < codeSnippet.length && (
                <span
                    style={{
                        background: '#000',
                        width: '1px',
                        display: 'inline-block',
                        animation: 'blink 1s step-start 0s infinite',
                    }}
                >
                    &nbsp;
                </span>
            )}
            <style>{`
                @keyframes blink {
                    50% { background: transparent; }
                }
            `}</style>
        </div>
    );
};

export default TypingArea;