import React, { useRef, useEffect } from 'react';
import useCodeShortcuts from '../utils/CodeShortcuts';

const CodeInputArea = ({
                           userCode,
                           containerRef,
                           handleCodeChange,
                           focusContainer,
                           isSubmitted
                       }) => {
    const {
        expandShortcut,
        duplicateLine,
        toggleLineComment,
        toggleBlockComment,
        handleAutoCloseQuote,
        handleAutoCloseParen,
        handleAutoCloseCurly,
        handleAutoCloseBracket
    } = useCodeShortcuts();
    const textareaRef = useRef(null);
    const highlightRef = useRef(null);

    // Function to highlight comments in the code
    const highlightCode = (code) => {
        // Escape HTML to prevent XSS
        const escapeHtml = (str) => {
            return str
                .replace(/&/g, '&')
                .replace(/</g, '<')
                .replace(/>/g, '>')
                .replace(/"/g, '"')
                .replace(/'/g, "'")
        };

        // Split code into lines
        const lines = code.split('\n');
        let inMultiLineComment = false;
        const highlightedLines = lines.map((line) => {
            let highlighted = escapeHtml(line);

            // Handle multi-line comments (/* ... */)
            if (inMultiLineComment) {
                if (line.includes('*/')) {
                    // End of multi-line comment
                    const endIndex = line.indexOf('*/') + 2;
                    highlighted = `<span class="comment">${escapeHtml(line.substring(0, endIndex))}</span>${escapeHtml(line.substring(endIndex))}`;
                    inMultiLineComment = false;
                } else {
                    // Entire line is within multi-line comment
                    highlighted = `<span class="comment">${highlighted}</span>`;
                }
            } else if (line.includes('/*')) {
                // Start of multi-line comment
                const startIndex = line.indexOf('/*');
                if (line.includes('*/')) {
                    // Single-line multi-line comment
                    const endIndex = line.indexOf('*/') + 2;
                    highlighted = `${escapeHtml(line.substring(0, startIndex))}<span class="comment">${escapeHtml(line.substring(startIndex, endIndex))}</span>${escapeHtml(line.substring(endIndex))}`;
                } else {
                    // Start of multi-line comment continuing to next lines
                    highlighted = `${escapeHtml(line.substring(0, startIndex))}<span class="comment">${escapeHtml(line.substring(startIndex))}</span>`;
                    inMultiLineComment = true;
                }
            } else if (line.trim().startsWith('//')) {
                // Single-line comment
                highlighted = `<span class="comment">${highlighted}</span>`;
            }

            return highlighted;
        });

        return highlightedLines.join('<br>');
    };

    // Sync textarea scroll with highlight div
    const syncScroll = () => {
        if (textareaRef.current && highlightRef.current) {
            highlightRef.current.scrollTop = textareaRef.current.scrollTop;
            highlightRef.current.scrollLeft = textareaRef.current.scrollLeft;
        }
    };

    // Update highlighted code when userCode changes
    useEffect(() => {
        if (highlightRef.current) {
            highlightRef.current.innerHTML = highlightCode(userCode);
        }
    }, [userCode]);

    const handleTextareaChange = (e) => {
        handleCodeChange(e.target.value);
    };

    const handleKeyDown = (e) => {
        const textarea = e.target;
        const start = textarea.selectionStart;
        const end = textarea.selectionEnd;

        // Ctrl+D: Duplicate line or selected lines
        if (e.ctrlKey && e.key === 'd') {
            e.preventDefault();
            const { newCode, newStart, newEnd } = duplicateLine(userCode, start, end);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = newStart;
                textarea.selectionEnd = newEnd;
            }, 0);
            return;
        }

        // Ctrl+/: Toggle line comment
        if (e.ctrlKey && e.key === '/') {
            e.preventDefault();
            const { newCode, newStart, newEnd } = toggleLineComment(userCode, start, end);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = newStart;
                textarea.selectionEnd = newEnd;
            }, 0);
            return;
        }

        // Ctrl+Shift+/: Toggle block comment
        if (e.ctrlKey && e.shiftKey && e.key === '/') {
            e.preventDefault();
            const { newCode, newStart, newEnd } = toggleBlockComment(userCode, start, end);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = newStart;
                textarea.selectionEnd = newEnd;
            }, 0);
            return;
        }

        // Tab: Indentation or shortcut expansion
        if (e.key === 'Tab') {
            e.preventDefault();
            const lines = userCode.split('\n');
            const startLine = userCode.substring(0, start).split('\n').length - 1;
            const endLine = userCode.substring(0, end).split('\n').length - 1;

            if (start !== end) {
                // Indent selected lines
                let newCode = '';
                let newStart = start;
                let newEnd = end;

                for (let i = 0; i < lines.length; i++) {
                    if (i >= startLine && i <= endLine) {
                        newCode += '    ' + lines[i] + (i < lines.length - 1 ? '\n' : '');
                        if (i === startLine) newStart += 4;
                        if (i < endLine) newEnd += 4;
                        else if (i === endLine) newEnd += 4;
                    } else {
                        newCode += lines[i] + (i < lines.length - 1 ? '\n' : '');
                    }
                }

                handleCodeChange(newCode);
                setTimeout(() => {
                    textarea.selectionStart = newStart;
                    textarea.selectionEnd = newEnd;
                }, 0);
            } else {
                // No selection, try to expand shortcut or insert tab
                const { newCode, newCursorPosition } = expandShortcut(userCode, start);
                handleCodeChange(newCode);
                setTimeout(() => {
                    textarea.selectionStart = textarea.selectionEnd = newCursorPosition;
                }, 0);
            }
            return;
        }

        // Auto-close quotes (double or single)
        if (e.key === '"' || e.key === "'") {
            e.preventDefault();
            const quoteType = e.key;
            const { newCode, newCursorPosition } = handleAutoCloseQuote(userCode, start, quoteType, textarea);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = textarea.selectionEnd = newCursorPosition;
            }, 0);
            return;
        }

        // Auto-close parentheses
        if (e.key === '(') {
            e.preventDefault();
            const { newCode, newCursorPosition } = handleAutoCloseParen(userCode, start, textarea);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = textarea.selectionEnd = newCursorPosition;
            }, 0);
            return;
        }

        // Auto-close curly braces
        if (e.key === '{') {
            e.preventDefault();
            const { newCode, newCursorPosition } = handleAutoCloseCurly(userCode, start, textarea);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = textarea.selectionEnd = newCursorPosition;
            }, 0);
            return;
        }

        // Auto-close square brackets
        if (e.key === '[') {
            e.preventDefault();
            const { newCode, newCursorPosition } = handleAutoCloseBracket(userCode, start, textarea);
            handleCodeChange(newCode);
            setTimeout(() => {
                textarea.selectionStart = textarea.selectionEnd = newCursorPosition;
            }, 0);
            return;
        }
    };

    return (
        <div className="code-input-container">
            <div className="bg-[var(--sliderhover)] rounded-lg p-4 border border-[var(--border-gray)] rounded-2xl shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h3 className="text-xl font-bold text-[var(--text)]">Your Code</h3>
                    <div className="text-base text-[var(--text)]">
                        Shortcuts: Tab for snippets (e.g., 'sout'), Ctrl+D to duplicate, Ctrl+/ to comment
                        </div>
                        </div>

                        <div
                        ref={containerRef}
                         className="code-editor-wrapper relative"
                         style={{ height: '450px' }}
                         onClick={focusContainer}
                    >
                        <div
                            ref={highlightRef}
                            className="absolute top-0 left-0 w-full h-full p-4 overflow-auto font-mono text-base leading-relaxed pointer-events-none"
                            style={{
                                fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace',
                                backgroundColor: 'var(--inbetween)',
                                color: 'var(--text)',
                                border: '1px solid var(--border-gray)',
                                borderRadius: '0.5rem',
                                whiteSpace: 'pre-wrap',
                                wordBreak: 'break-all',
                            }}
                        />
                        <textarea
                            ref={textareaRef}
                            value={userCode}
                            onChange={handleTextareaChange}
                            onKeyDown={handleKeyDown}
                            onScroll={syncScroll}
                            placeholder="Write your code here..."
                            disabled={isSubmitted}
                            spellCheck="false"
                            className="absolute top-0 left-0 w-full h-full p-4 rounded-lg resize-none font-mono text-base leading-relaxed focus:outline-none disabled:cursor-not-allowed
                       focus:ring-2 focus:ring-[var(--accent)] focus:ring-offset-2 focus:ring-offset-[var(--background)]"
                            style={{
                                fontFamily: 'Monaco, Menlo, "Ubuntu Mono", monospace',
                                backgroundColor: 'transparent',
                                color: 'transparent',
                                caretColor: 'var(--text)',
                                border: '1px solid var(--border-gray)',
                                borderRadius: '0.5rem'
                            }}
                        />
                        <style>
                            {`
              .comment {
                color: var(--comment); /* Use theme-defined comment color */
              }
            `}
                        </style>
                        <div className="mt-2 text-base text-[var(--text)]">
                            Lines: {userCode.split('\n').length} | Characters: {userCode.length}
                        </div>
                    </div>
                </div>
            </div>
            );
            };

            export default CodeInputArea;