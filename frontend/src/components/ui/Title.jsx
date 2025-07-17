import React from 'react';
import { useTheme } from '../../styles/ThemeContext';

const Title = ({ fontWeight = 'bold', fontSize = '4xl' }) => {
    const { theme } = useTheme(); // Access the current theme

    // Map fontSize to Tailwind CSS classes or fallback to custom style
    const sizeClass = fontSize.startsWith('text-') ? fontSize : `text-${fontSize}`;

    return (
        <span
            className={`text-[var(--accent)] font-montserrat ${sizeClass}`}
            style={{ fontWeight }}
        >
            codegobrr
        </span>
    );
};

export default Title;