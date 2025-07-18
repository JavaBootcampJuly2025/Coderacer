import '../App.css';
import logo from '../assets/icon.png';
import { useRef, useEffect } from 'react';

function Welcome() {
    const containerRef = useRef(null);

    // useEffect(() => {
    //     const container = containerRef.current;
    //     if (!container) return;
    //
    //     const handleMouseMove = (e) => {
    //         const rect = container.getBoundingClientRect();
    //         const centerX = rect.left + rect.width / 2;
    //         const centerY = rect.top + rect.height / 2;
    //         const mouseX = e.clientX;
    //         const mouseY = e.clientY;
    //
    //         // Calculate tilt angles based on mouse position relative to center
    //         const maxTilt = 4; // Reduced max tilt for smoother, subtler sway
    //         const tiltX = ((mouseY - centerY) / rect.height) * maxTilt;
    //         const tiltY = ((centerX - mouseX) / rect.width) * maxTilt;
    //
    //         // Calculate reflection effect with more noticeable color/opacity shift
    //         const gradientShift = ((mouseX - centerX) / rect.width) * 0.2; // Increased range: -0.2 to 0.2
    //         const baseOpacity = 0.15;
    //         const highlightOpacity = Math.min(0.4, baseOpacity + gradientShift + 0.1); // Increased max opacity for brighter reflection
    //         const tint = gradientShift > 0 ? '220, 220, 255' : '200, 180, 255'; // Stronger bluish-purple tint shift
    //
    //         // Apply transform for sway and background for reflection
    //         container.style.transform = `perspective(1200px) rotateX(${tiltX}deg) rotateY(${tiltY}deg)`; // Increased perspective for smoother 3D effect
    //         container.style.background = `linear-gradient(135deg, rgba(${tint}, ${highlightOpacity}) 0%, rgba(255, 255, 255, ${Math.max(0.1, baseOpacity - gradientShift)}) 100%)`;
    //     };
    //
    //     const handleMouseLeave = () => {
    //         // Reset transform and background when mouse leaves
    //         container.style.transform = 'perspective(1200px) rotateX(0deg) rotateY(0deg)';
    //         container.style.background = 'rgba(255, 255, 255, 0.15)';
    //     };
    //
    //     container.addEventListener('mousemove', handleMouseMove);
    //     container.addEventListener('mouseleave', handleMouseLeave);
    //
    //     return () => {
    //         container.removeEventListener('mousemove', handleMouseMove);
    //         container.removeEventListener('mouseleave', handleMouseLeave);
    //     };
    // }, []);

    return (
        <div className="home-wrapper">
            <div className="content-glass" ref={containerRef}>
                <h1 className="header">Coderacer</h1>
                <img className="logo" src={logo} alt="Coderacer logo" />
                <div className="button-group">
                    <a href="/Login">
                        <button className="action">Login</button>
                    </a>
                    <a href="/Register">
                        <button className="action">Register</button>
                    </a>
                </div>
                <a href="/Home">
                    <button className="action single-button">Home</button>
                </a>
            </div>
        </div>
    );
}

export default Welcome;