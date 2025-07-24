import { useCallback } from 'react';

// Define shortcut mappings for Java
const SHORTCUT_MAPPINGS = {
    'sout': 'System.out.println()',
    'main': 'public static void main(String[] args) {}',
    'fori': 'for (int i = 0; i < ; i++) {}',
    'if': 'if () {}',
    'else': 'else {}',
    'while': 'while () {}',
    'class': 'public class  {}',
};

// Utility hook for code editor shortcuts
const useCodeShortcuts = () => {
    // Expand Tab-based shortcuts (e.g., sout -> System.out.println())
    const expandShortcut = useCallback((code, cursorPosition) => {
        const beforeCursor = code.substring(0, cursorPosition);
        const words = beforeCursor.split(/[\s\n]+/);
        const lastWord = words[words.length - 1];

        if (SHORTCUT_MAPPINGS[lastWord]) {
            const replacement = SHORTCUT_MAPPINGS[lastWord];
            const newCode =
                code.substring(0, cursorPosition - lastWord.length) +
                replacement +
                code.substring(cursorPosition);

            let newCursorPosition = cursorPosition - lastWord.length + replacement.length;
            const parenIndex = replacement.indexOf('()');
            const braceIndex = replacement.indexOf('{}');

            if (parenIndex !== -1 && (braceIndex === -1 || parenIndex < braceIndex)) {
                newCursorPosition = cursorPosition - lastWord.length + parenIndex + 1;
            } else if (braceIndex !== -1) {
                newCursorPosition = cursorPosition - lastWord.length + braceIndex + 1;
            }

            return { newCode, newCursorPosition };
        }

        return {
            newCode: code.substring(0, cursorPosition) + '    ' + code.substring(cursorPosition),
            newCursorPosition: cursorPosition + 4,
        };
    }, []);

    // Duplicate line or selected lines (Ctrl+D)
    const duplicateLine = useCallback((code, start, end) => {
        const lines = code.split('\n');
        const startLine = code.substring(0, start).split('\n').length - 1;
        const endLine = code.substring(0, end).split('\n').length - 1;
        let newCode = code;
        let newStart = start;
        let newEnd = end;

        if (start === end) {
            const currentLine = lines[startLine];
            const lineStartPos = code.lastIndexOf('\n', start - 1) + 1;
            const lineEndPos = code.indexOf('\n', start) === -1 ? code.length : code.indexOf('\n', start);
            newCode = code.substring(0, lineEndPos) + '\n' + currentLine + code.substring(lineEndPos);
            newStart = start + currentLine.length + 1;
            newEnd = newStart;
        } else {
            const selectedLines = lines.slice(startLine, endLine + 1).join('\n');
            const selectionEndPos = end;
            newCode = code.substring(0, selectionEndPos) + '\n' + selectedLines + code.substring(selectionEndPos);
            newStart = start + selectedLines.length + 1;
            newEnd = end + selectedLines.length + 1;
        }

        return { newCode, newStart, newEnd };
    }, []);

    // Toggle line comment (Ctrl+/)
    const toggleLineComment = useCallback((code, start, end) => {
        const lines = code.split('\n');
        const startLine = code.substring(0, start).split('\n').length - 1;
        const endLine = code.substring(0, end).split('\n').length - 1;
        let newCode = '';
        let newStart = start;
        let newEnd = end;

        for (let i = 0; i < lines.length; i++) {
            if (i >= startLine && i <= endLine) {
                if (lines[i].trim().startsWith('//')) {
                    newCode += lines[i].replace(/^\/\/\s?/, '') + (i < lines.length - 1 ? '\n' : '');
                    if (i === startLine) newStart -= Math.min(lines[i].indexOf(lines[i].trim()), 2);
                    if (i <= endLine) newEnd -= Math.min(lines[i].indexOf(lines[i].trim()), 2);
                } else {
                    newCode += '// ' + lines[i] + (i < lines.length - 1 ? '\n' : '');
                    if (i === startLine) newStart += 3;
                    if (i <= endLine) newEnd += 3;
                }
            } else {
                newCode += lines[i] + (i < lines.length - 1 ? '\n' : '');
            }
        }

        return { newCode, newStart, newEnd };
    }, []);

    // Toggle block comment (Ctrl+Shift+/)
    const toggleBlockComment = useCallback((code, start, end) => {
        let newCode = code;
        let newStart = start;
        let newEnd = end;

        if (start === end) {
            const lines = code.split('\n');
            const startLine = code.substring(0, start).split('\n').length - 1;
            const currentLine = lines[startLine];
            const lineStartPos = code.lastIndexOf('\n', start - 1) + 1;
            const lineEndPos = code.indexOf('\n', start) === -1 ? code.length : code.indexOf('\n', start);
            const lineText = code.substring(lineStartPos, lineEndPos);

            if (lineText.trim().startsWith('/*') && lineText.trim().endsWith('*/')) {
                const uncommented = lineText.replace(/^\/\*\s?/, '').replace(/\s?\*\/$/, '');
                newCode = code.substring(0, lineStartPos) + uncommented + code.substring(lineEndPos);
                newStart -= 2;
                newEnd -= 4;
            } else {
                newCode = code.substring(0, lineStartPos) + '/* ' + lineText + ' */' + code.substring(lineEndPos);
                newStart += 3;
                newEnd += 7;
            }
        } else {
            const selectedText = code.substring(start, end);
            if (selectedText.startsWith('/*') && selectedText.endsWith('*/')) {
                newCode = code.substring(0, start) + selectedText.slice(2, -2) + code.substring(end);
                newStart -= 2;
                newEnd -= 4;
            } else {
                newCode = code.substring(0, start) + '/* ' + selectedText + ' */' + code.substring(end);
                newStart += 3;
                newEnd += 7;
            }
        }

        return { newCode, newStart, newEnd };
    }, []);

    // Handle auto-closing quotes (typing " or ' inserts closing quote if appropriate)
    const handleAutoCloseQuote = useCallback((code, cursorPosition, quoteType, textarea) => {
        // No auto-closing if there's a selection
        if (textarea.selectionStart !== textarea.selectionEnd) {
            return {
                newCode: code.substring(0, cursorPosition) + quoteType + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }

        const nextChar = code[cursorPosition] || '';
        const isNextCharClosingQuote = nextChar === quoteType;
        const isNextCharValidForAutoClose = !nextChar || /[\s\n;,.)}\]]/.test(nextChar);

        // Check if cursor is inside an existing string by scanning backward for an unclosed quote
        let isInsideString = false;
        let i = cursorPosition - 1;
        let quoteCount = 0;
        while (i >= 0) {
            if (code[i] === quoteType && (i === 0 || code[i - 1] !== '\\')) {
                quoteCount++;
            }
            i--;
        }
        isInsideString = quoteCount % 2 === 1;

        if (isInsideString || isNextCharClosingQuote || !isNextCharValidForAutoClose) {
            // Insert single quote if inside a string, next to a closing quote, or next to a non-valid character
            return {
                newCode: code.substring(0, cursorPosition) + quoteType + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        } else {
            // Insert both quotes and place cursor in the middle
            return {
                newCode: code.substring(0, cursorPosition) + quoteType + quoteType + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }
    }, []);

    // Handle auto-closing parentheses (typing ( inserts closing ) if appropriate)
    const handleAutoCloseParen = useCallback((code, cursorPosition, textarea) => {
        // No auto-closing if there's a selection
        if (textarea.selectionStart !== textarea.selectionEnd) {
            return {
                newCode: code.substring(0, cursorPosition) + '(' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }

        const nextChar = code[cursorPosition] || '';
        const isNextCharClosingParen = nextChar === ')';
        const isNextCharValidForAutoClose = !nextChar || /[\s\n;,.)}\]]/.test(nextChar);

        // Check if cursor is inside a string (either "..." or '...')
        let isInsideString = false;
        let i = cursorPosition - 1;
        let doubleQuoteCount = 0;
        let singleQuoteCount = 0;
        while (i >= 0) {
            if (code[i] === '"' && (i === 0 || code[i - 1] !== '\\')) {
                doubleQuoteCount++;
            } else if (code[i] === "'" && (i === 0 || code[i - 1] !== '\\')) {
                singleQuoteCount++;
            }
            i--;
        }
        isInsideString = doubleQuoteCount % 2 === 1 || singleQuoteCount % 2 === 1;

        if (isInsideString || isNextCharClosingParen || !isNextCharValidForAutoClose) {
            // Insert single parenthesis if inside a string, next to a closing parenthesis, or next to a non-valid character
            return {
                newCode: code.substring(0, cursorPosition) + '(' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        } else {
            // Insert both parentheses and place cursor in the middle
            return {
                newCode: code.substring(0, cursorPosition) + '()' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }
    }, []);

    // Handle auto-closing curly braces (typing { inserts closing } if appropriate)
    const handleAutoCloseCurly = useCallback((code, cursorPosition, textarea) => {
        // No auto-closing if there's a selection
        if (textarea.selectionStart !== textarea.selectionEnd) {
            return {
                newCode: code.substring(0, cursorPosition) + '{' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }

        const nextChar = code[cursorPosition] || '';
        const isNextCharClosingCurly = nextChar === '}';
        const isNextCharValidForAutoClose = !nextChar || /[\s\n;,.)}\]]/.test(nextChar);

        // Check if cursor is inside a string (either "..." or '...')
        let isInsideString = false;
        let i = cursorPosition - 1;
        let doubleQuoteCount = 0;
        let singleQuoteCount = 0;
        while (i >= 0) {
            if (code[i] === '"' && (i === 0 || code[i - 1] !== '\\')) {
                doubleQuoteCount++;
            } else if (code[i] === "'" && (i === 0 || code[i - 1] !== '\\')) {
                singleQuoteCount++;
            }
            i--;
        }
        isInsideString = doubleQuoteCount % 2 === 1 || singleQuoteCount % 2 === 1;

        if (isInsideString || isNextCharClosingCurly || !isNextCharValidForAutoClose) {
            // Insert single curly brace if inside a string, next to a closing curly, or next to a non-valid character
            return {
                newCode: code.substring(0, cursorPosition) + '{' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        } else {
            // Insert both curly braces and place cursor in the middle
            return {
                newCode: code.substring(0, cursorPosition) + '{}' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }
    }, []);

    // Handle auto-closing square brackets (typing [ inserts closing ] if appropriate)
    const handleAutoCloseBracket = useCallback((code, cursorPosition, textarea) => {
        // No auto-closing if there's a selection
        if (textarea.selectionStart !== textarea.selectionEnd) {
            return {
                newCode: code.substring(0, cursorPosition) + '[' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }

        const nextChar = code[cursorPosition] || '';
        const isNextCharClosingBracket = nextChar === ']';
        const isNextCharValidForAutoClose = !nextChar || /[\s\n;,.)}\]]/.test(nextChar);

        // Check if cursor is inside a string (either "..." or '...')
        let isInsideString = false;
        let i = cursorPosition - 1;
        let doubleQuoteCount = 0;
        let singleQuoteCount = 0;
        while (i >= 0) {
            if (code[i] === '"' && (i === 0 || code[i - 1] !== '\\')) {
                doubleQuoteCount++;
            } else if (code[i] === "'" && (i === 0 || code[i - 1] !== '\\')) {
                singleQuoteCount++;
            }
            i--;
        }
        isInsideString = doubleQuoteCount % 2 === 1 || singleQuoteCount % 2 === 1;

        if (isInsideString || isNextCharClosingBracket || !isNextCharValidForAutoClose) {
            // Insert single square bracket if inside a string, next to a closing bracket, or next to a non-valid character
            return {
                newCode: code.substring(0, cursorPosition) + '[' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        } else {
            // Insert both square brackets and place cursor in the middle
            return {
                newCode: code.substring(0, cursorPosition) + '[]' + code.substring(cursorPosition),
                newCursorPosition: cursorPosition + 1,
            };
        }
    }, []);

    return {
        expandShortcut,
        duplicateLine,
        toggleLineComment,
        toggleBlockComment,
        handleAutoCloseQuote,
        handleAutoCloseParen,
        handleAutoCloseCurly,
        handleAutoCloseBracket
    };
};

export default useCodeShortcuts;