// src/styles/colors.js
const themes = {
    dark: {
        defaultChar: '#888',
        correctChar: '#87f4ff',
        incorrectChar: '#cd0b0b',
        unwrittenChar: '#2a6a9f', // Added for unwritten text
        caret: '#ffbe26',
        primaryButton: '#284475',
        primaryButtonHover: '#344e7a',
        background: '#13223A',
        text: '#87f4ff',
        accent: '#87f4ff',
        leaderboardBg: 'rgba(0, 0, 0, 0.25)',
        border: 'rgba(255, 255, 255, 1)',
        footerText: '#ccc',
        borderGray: '#1a4971',
        inbetween: '#1d365f',
        light: '#164270',
        transparent: 'rgba(0,0,0,0)',
        black: 'rgba(0,0,0,0.5)',
        sliderhover: '#222e45'
    },
    light: {
        defaultChar: '#aaa',
        correctChar: '#78095a',
        incorrectChar: '#cd0b0b',
        unwrittenChar: '#b19bac', // Lighter green for dark theme
        caret: '#1e2742',
        primaryButton: '#d5c1e8',
        primaryButtonHover: '#d8c6ea',
        background: '#e2dbe8',
        text: '#78095a',
        accent: '#78095a',
        leaderboardBg: 'rgba(0,0,0,0.1)',
        border: 'rgba(255, 255, 255, 0.5)',
        footerText: '#1e2742',
        borderGray: '#555',
        inbetween: '#c8acd4',
        light: '#164270',
        transparent: 'rgba(0,0,0,0)',
        black: 'rgba(0,0,0,0.22)',
        sliderhover: '#b598ba'
    },
};

export default themes;