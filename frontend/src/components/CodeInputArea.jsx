import React from 'react';

const CodeInputArea = ({
                           userCode,
                           containerRef,
                           handleCodeChange,
                           focusContainer,
                           isSubmitted
                       }) => {
    const handleTextareaChange = (e) => {
        handleCodeChange(e.target.value);
    };

    const handleKeyDown = (e) => {
        // Handle tab key for indentation
        if (e.key === 'Tab') {
            e.preventDefault();
            const textarea = e.target;
            const start = textarea.selectionStart;
            const end = textarea.selectionEnd;

            // Insert tab character
            const newValue = userCode.substring(0, start) + '    ' + userCode.substring(end);
            handleCodeChange(newValue);

            // Set cursor position after the inserted tab
            setTimeout(() => {
                textarea.selectionStart = textarea.selectionEnd = start + 4;
            }, 0);
        }
    };

    return (
        <div className="code-input-container h-full">
            <div className="bg-[var(--sliderhover)] rounded-lg p-4 h-full border border-[var(--border-gray)] rounded-2xl shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-xl font-bold text-[var(--text)]">Your Code</h3>
                    <div className="text-base text-[var(--text)]">
                        Press Tab for indentation
                    </div>
                </div>

                <div
                    ref={containerRef}
                    className="code-editor-wrapper h-full"
                    onClick={focusContainer}
                >
                    <textarea
                        value={userCode}
                        onChange={handleTextareaChange}
                        onKeyDown={handleKeyDown}
                        placeholder="Write your code here..."
                        disabled={isSubmitted}
                        className="w-full h-96 p-4 rounded-lg resize-none font-mono text-base leading-relaxed focus:outline-none disabled:cursor-not-allowed
                                   focus:ring-2 focus:ring-[var(--accent)] focus:ring-offset-2 focus:ring-offset-[var(--background)]"
                        style={{
                            minHeight: '400px',
                            tabSize: 4,
                            fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace',
                            backgroundColor: 'var(--inbetween)', // Use 'inbetween' for shading
                            color: 'var(--text)',
                            border: '1px solid var(--border-gray)',
                            borderRadius: '0.5rem'
                        }}
                    />
                    <div className="mt-2 text-base text-[var(--text)]">
                        Lines: {userCode.split('\n').length} |
                        Characters: {userCode.length}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CodeInputArea;