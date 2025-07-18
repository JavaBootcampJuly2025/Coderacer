// src/components/Footer.jsx
import React from 'react';
import colors from '../styles/colors'; // Import colors

const Footer = () => {
    return (
        <footer style={{ marginTop: '2rem', fontSize: '0.9rem', color: colors.footerText, textAlign: 'center' }}>
            <p>© 2025 Coderacer Inc. All rights reserved.</p>
        </footer>
    );
};

export default Footer;