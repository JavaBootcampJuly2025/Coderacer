import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { ThemeProvider } from './styles/ThemeContext';
import { LevelProvider } from './context/LevelContext';
import App from './App';
import './styles/global.css';
import React from 'react';

const container = document.getElementById('root');
const root = createRoot(container);
class ErrorBoundary extends React.Component {
    state = { error: null };
    static getDerivedStateFromError(error) {
        return { error };
    }
    render() {
        if (this.state.error) {
            console.error('ErrorBoundary caught:', this.state.error);
            return <h1>Something went wrong.</h1>;
        }
        return this.props.children;
    }
}

// In index.js
root.render(
    <StrictMode>
        <BrowserRouter>
            <ThemeProvider>
                <LevelProvider>
                    <ErrorBoundary>
                        <App />
                    </ErrorBoundary>
                </LevelProvider>
            </ThemeProvider>
        </BrowserRouter>
    </StrictMode>
);