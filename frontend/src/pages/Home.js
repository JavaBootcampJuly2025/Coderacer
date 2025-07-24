import React, { useEffect, useState, useRef } from 'react';
import Header from '../components/Header';
import SpeedChart from '../components/SpeedChart';
import RightPanel from '../components/RightPanel';
import Footer from '../components/Footer';
import Leaderboard from '../components/Leaderboard';
import UserMetrics from '../components/UserMetrics';
import ParticleSystem from '../components/ParticleSystem';
import '../App.css';
import { useLevelContext } from '../context/LevelContext';
import { useTheme } from '../styles/ThemeContext';
import LevelSwitch from "../components/ui/LevelSwitch";

const Home = () => {
    const { latestSession } = useLevelContext();
    const { theme, themes } = useTheme();
    const [isLoaded, setIsLoaded] = useState(false);
    const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
    const [ripples, setRipples] = useState([]);
    const [scrollY, setScrollY] = useState(0);
    const rippleTimeoutRef = useRef();

    useEffect(() => {
        setIsLoaded(true);
        
        const handleMouseMove = (e) => {
            setMousePosition({ x: e.clientX, y: e.clientY });
        };

        const handleScroll = () => {
            setScrollY(window.scrollY);
        };

        window.addEventListener('mousemove', handleMouseMove);
        window.addEventListener('scroll', handleScroll);
        
        return () => {
            window.removeEventListener('mousemove', handleMouseMove);
            window.removeEventListener('scroll', handleScroll);
        };
    }, []);

    const handleClick = (e) => {
        document.activeElement.blur();
        
        // Create ripple effect
        const newRipple = {
            id: Date.now(),
            x: e.clientX,
            y: e.clientY,
        };
        
        setRipples(prev => [...prev, newRipple]);
        
        // Remove ripple after animation
        setTimeout(() => {
            setRipples(prev => prev.filter(ripple => ripple.id !== newRipple.id));
        }, 1000);
    };

    // Get current theme colors
    const currentTheme = themes[theme];

    const speedLog = latestSession?.speedLog || [{ time: 0, rawCpm: 0, accurateWpm: 0 }, { time: 10, rawCpm: 50, accurateWpm: 40 }];
    const endTime = latestSession?.endTime || 10;
    const totalTyped = latestSession?.totalTyped || 0;
    const mistakes = latestSession?.mistakes || 0;
    const userInput = latestSession?.userInput || '';
    const codeSnippet = latestSession?.codeSnippet || '';

    return (
        <div
            className="home-wrapper-enhanced"
            onClick={handleClick}
            data-theme={theme}
        >
            {/* Particle System */}
            <ParticleSystem />

            {/* Dynamic background with theme-aware animations */}
            <div className="background-layer">
                {/* Animated orbs with parallax */}
                <div 
                    className="bg-orb bg-orb-1"
                    style={{
                        transform: `translateY(${scrollY * 0.1}px)`,
                        background: `radial-gradient(circle, ${currentTheme.accent}20, transparent)`
                    }}
                ></div>
                <div 
                    className="bg-orb bg-orb-2"
                    style={{
                        transform: `translateY(${scrollY * 0.15}px)`,
                        background: `radial-gradient(circle, ${currentTheme.accent}15, transparent)`
                    }}
                ></div>
                <div 
                    className="bg-orb bg-orb-3"
                    style={{
                        transform: `translateY(${scrollY * 0.2}px)`,
                        background: `radial-gradient(circle, ${currentTheme.accent}25, transparent)`
                    }}
                ></div>
                
                {/* Animated grid with theme colors */}
                <div 
                    className="bg-grid"
                    style={{
                        backgroundImage: `
                            linear-gradient(${currentTheme.borderGray}40 1px, transparent 1px),
                            linear-gradient(90deg, ${currentTheme.borderGray}40 1px, transparent 1px)
                        `
                    }}
                ></div>
                
                {/* Enhanced mouse follower with theme colors */}
                <div 
                    className="mouse-glow"
                    style={{
                        left: mousePosition.x - 100,
                        top: mousePosition.y - 100,
                        background: `radial-gradient(circle, ${currentTheme.accent}10, transparent)`,
                        transition: 'all 0.1s ease'
                    }}
                ></div>

                {/* Floating geometric shapes */}
                <div className="floating-shapes">
                    {[...Array(6)].map((_, i) => (
                        <div
                            key={i}
                            className={`floating-shape shape-${i + 1}`}
                            style={{
                                background: `${currentTheme.accent}15`,
                                border: `1px solid ${currentTheme.accent}30`,
                                animationDelay: `${i * 2}s`
                            }}
                        />
                    ))}
                </div>
            </div>

            {/* Click ripple effects */}
            {ripples.map(ripple => (
                <div
                    key={ripple.id}
                    className="click-ripple"
                    style={{
                        left: ripple.x - 25,
                        top: ripple.y - 25,
                        borderColor: currentTheme.accent
                    }}
                />
            ))}

            {/* Header with enhanced animations */}
            <div className={`header-container ${isLoaded ? 'loaded' : ''}`}>
                <Header />
            </div>

            {/* Main Content - Clean Layout with enhanced effects */}
            <div className="main-content-wrapper">
                <div className={`content-grid ${isLoaded ? 'loaded' : ''}`}>
                    
                    {/* Left Column - Leaderboard with hover effects */}
                    <div className="leaderboard-section">
                        <Leaderboard />
                    </div>

                    {/* Center Column */}
                    <div className="center-column">
                        
                        {/* Top Row - Game Panels with stagger animation */}
                        <div className="game-panels-row">
                            <div className="game-panel">
                                <RightPanel />
                            </div>
                            <div className="metrics-panel">
                                <UserMetrics />
                            </div>
                        </div>

                        {/* Bottom Row - Chart with enhanced effects */}
                        <div className="chart-section">
                            <SpeedChart
                                endTime={endTime}
                                speedLog={speedLog}
                                totalTyped={totalTyped}
                                mistakes={mistakes}
                                userInput={userInput}
                                codeSnippet={codeSnippet}
                            />
                        </div>
                    </div>
                </div>
            </div>

            {/* Footer with enhanced styling */}
            <div className={`footer-container ${isLoaded ? 'loaded' : ''}`}>
                <Footer />
            </div>

            {/* Theme transition overlay */}
            <div className="theme-transition-overlay"></div>
        </div>
    );
};

export default Home;

