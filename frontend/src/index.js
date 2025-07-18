// src/index.js
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from './styles/ThemeContext';
import { LevelProvider } from './context/LevelContext'; // Adjusted path
import App from './App';
import './styles/global.css';

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
    <StrictMode>
        <BrowserRouter>
            <ThemeProvider>
                <LevelProvider>
                    <App />
                </LevelProvider>
            </ThemeProvider>
        </BrowserRouter>
    </StrictMode>
);