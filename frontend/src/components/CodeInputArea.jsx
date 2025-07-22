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
            <div className="bg-[#1E3A5F] rounded-lg p-4 h-full shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-lg font-semibold text-white">Your Code</h3>
                    <div className="text-sm text-gray-400">
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
                        className={`
                            w-full h-96 p-4 bg-[#0F1419] text-gray-100 
                            border border-gray-600 rounded-lg resize-none
                            font-mono text-sm leading-relaxed
                            focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500
                            disabled:bg-gray-800 disabled:cursor-not-allowed
                            placeholder-gray-500
                        `}
                        style={{
                            minHeight: '400px',
                            tabSize: 4,
                            fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace'
                        }}
                    />
                </div>

                <div className="mt-2 text-xs text-gray-400">
                    Lines: {userCode.split('\n').length} |
                    Characters: {userCode.length}
                </div>
            </div>
        </div>
    );
};

export default CodeInputArea;