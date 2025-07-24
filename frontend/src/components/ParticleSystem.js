import React, { useEffect, useRef } from 'react';
import { useTheme } from '../styles/ThemeContext';

const ParticleSystem = () => {
    const canvasRef = useRef(null);
    const { theme, themes } = useTheme();
    const animationRef = useRef();
    const particlesRef = useRef([]);

    useEffect(() => {
        const canvas = canvasRef.current;
        if (!canvas) return;

        const ctx = canvas.getContext('2d');
        const particles = particlesRef.current;

        // Set canvas size
        const resizeCanvas = () => {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        };

        resizeCanvas();
        window.addEventListener('resize', resizeCanvas);

        // Particle class
        class Particle {
            constructor() {
                this.reset();
                this.y = Math.random() * canvas.height;
                this.opacity = Math.random() * 0.5 + 0.2;
            }

            reset() {
                this.x = Math.random() * canvas.width;
                this.y = -10;
                this.size = Math.random() * 3 + 1;
                this.speedX = (Math.random() - 0.5) * 0.5;
                this.speedY = Math.random() * 1 + 0.5;
                this.opacity = Math.random() * 0.5 + 0.2;
                this.life = 1;
                this.decay = Math.random() * 0.005 + 0.002;
            }

            update() {
                this.x += this.speedX;
                this.y += this.speedY;
                this.life -= this.decay;
                this.opacity = this.life * 0.5;

                if (this.life <= 0 || this.y > canvas.height + 10) {
                    this.reset();
                }
            }

            draw() {
                ctx.save();
                ctx.globalAlpha = this.opacity;
                
                // Get theme colors
                const currentTheme = themes[theme];
                const accentColor = currentTheme.accent;
                
                // Convert hex to RGB for gradient
                const hexToRgb = (hex) => {
                    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
                    return result ? {
                        r: parseInt(result[1], 16),
                        g: parseInt(result[2], 16),
                        b: parseInt(result[3], 16)
                    } : null;
                };

                const rgb = hexToRgb(accentColor);
                if (rgb) {
                    const gradient = ctx.createRadialGradient(
                        this.x, this.y, 0,
                        this.x, this.y, this.size
                    );
                    gradient.addColorStop(0, `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, ${this.opacity})`);
                    gradient.addColorStop(1, `rgba(${rgb.r}, ${rgb.g}, ${rgb.b}, 0)`);
                    
                    ctx.fillStyle = gradient;
                    ctx.beginPath();
                    ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
                    ctx.fill();
                }
                
                ctx.restore();
            }
        }

        // Initialize particles
        const initParticles = () => {
            particles.length = 0;
            const particleCount = Math.min(50, Math.floor(canvas.width / 20));
            for (let i = 0; i < particleCount; i++) {
                particles.push(new Particle());
            }
        };

        initParticles();

        // Animation loop
        const animate = () => {
            ctx.clearRect(0, 0, canvas.width, canvas.height);

            particles.forEach(particle => {
                particle.update();
                particle.draw();
            });

            animationRef.current = requestAnimationFrame(animate);
        };

        animate();

        // Mouse interaction
        const handleMouseMove = (e) => {
            const rect = canvas.getBoundingClientRect();
            const mouseX = e.clientX - rect.left;
            const mouseY = e.clientY - rect.top;

            // Add particles near mouse
            if (Math.random() < 0.3) {
                const particle = new Particle();
                particle.x = mouseX + (Math.random() - 0.5) * 50;
                particle.y = mouseY + (Math.random() - 0.5) * 50;
                particle.size = Math.random() * 2 + 1;
                particle.opacity = 0.8;
                particles.push(particle);

                // Remove excess particles
                if (particles.length > 100) {
                    particles.shift();
                }
            }
        };

        canvas.addEventListener('mousemove', handleMouseMove);

        return () => {
            window.removeEventListener('resize', resizeCanvas);
            canvas.removeEventListener('mousemove', handleMouseMove);
            if (animationRef.current) {
                cancelAnimationFrame(animationRef.current);
            }
        };
    }, [theme, themes]);

    return (
        <canvas
            ref={canvasRef}
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                width: '100%',
                height: '100%',
                pointerEvents: 'none',
                zIndex: 1,
                opacity: 0.6
            }}
        />
    );
};

export default ParticleSystem;

