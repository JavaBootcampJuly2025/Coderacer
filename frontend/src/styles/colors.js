// src/styles/colors.js
const themes = {
    dark: {
        defaultChar: '#888',
        correctChar: '#24E5B7',
        incorrectChar: '#cd0b0b',
        unwrittenChar: '#2a6a9f', // Added for unwritten text
        caret: '#ffbe26',
        primaryButton: '#174065',
        primaryButtonHover: '#1a4971',
        background: '#13223A',
        text: '#fff',
        accent: '#24E5B7',
        leaderboardBg: 'rgba(0, 0, 0, 0.25)',
        border: 'rgba(255, 255, 255, 1)',
        footerText: '#ccc',
        borderGray: '#1a4971',
    },
    light: {
        defaultChar: '#aaa',
        correctChar: '#a5538f',
        incorrectChar: '#cd0b0b',
        unwrittenChar: '#b19bac', // Lighter green for dark theme
        caret: '#1e2742',
        primaryButton: '#c9c0ef',
        primaryButtonHover: '#9e96d3',
        background: '#e2dbe8',
        text: '#1e2742',
        accent: '#a56894',
        leaderboardBg: 'rgba(0,0,0,0.1)',
        border: 'rgba(255, 255, 255, 0.5)',
        footerText: '#1e2742',
        borderGray: '#555',
    },
};

export default themes;