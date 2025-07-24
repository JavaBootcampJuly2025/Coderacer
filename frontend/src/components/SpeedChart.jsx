import React from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, ReferenceLine } from 'recharts';
import { useTheme } from '../styles/ThemeContext';
import themes from '../styles/colors';

// Custom Tooltip component with stable positioning
const CustomTooltip = ({ active, payload, label }) => {
    const { theme } = useTheme();
    const selectedTheme = themes[theme];

    if (active && payload && payload.length) {
        return (
            <div
                style={{
                    backgroundColor: selectedTheme.leaderboardBg,
                    color: selectedTheme.text,
                    padding: '10px',
                    borderRadius: '5px',
                    border: `1px solid ${selectedTheme.border}`,
                    boxShadow: '0 2px 8px rgba(0, 0, 0, 0.2)',
                    position: 'relative',
                    pointerEvents: 'none',
                }}
            >
                <p style={{ margin: '0 0 5px 0', fontWeight: 'bold' }}>Time: {label} s</p>
                {payload.map((entry, index) => (
                    <p key={index} style={{ margin: '0', color: entry.stroke }}>
                        {entry.name}: {entry.value} WPM
                    </p>
                ))}
            </div>
        );
    }
    return null;
};

const SpeedChart = ({ endTime, speedLog, totalTyped, mistakes, userInput, codeSnippet }) => {
    const { theme } = useTheme();
    const selectedTheme = themes[theme];

    console.log('SpeedChart props:', { endTime, speedLog, totalTyped, mistakes, userInput, codeSnippet });

    // Prepare data: use default empty data if no valid data
    const defaultData = [{ time: 0, rawWpm: 0, accurateWpm: 0, startTime: 0 }];
    const validatedSpeedLog = (!endTime || !speedLog || speedLog.length <= 1)
        ? defaultData
        : speedLog.map(entry => ({
            time: Number(entry.time) || 0,
            rawWpm: Number(entry.rawWpm) || 0,
            accurateWpm: Number(entry.accurateWpm) || 0,
            startTime: Number(entry.startTime) || 0,
        }));

    // Calculate duration in seconds
    const startTime = validatedSpeedLog[0]?.startTime || 0;
    const duration = (!endTime || !speedLog || speedLog.length <= 1)
        ? 10 // Default duration for empty chart
        : Math.max(1, Math.floor((endTime - startTime) / 1000));
    const tickInterval = duration <= 30 ? 1 : duration <= 60 ? 2 : Math.ceil(duration / 15);
    const ticks = Array.from(
        { length: Math.floor(duration / tickInterval) + 1 },
        (_, i) => i * tickInterval
    );

    // Debug ticks
    console.log('Ticks:', ticks);
    console.log('Duration:', duration, 'StartTime:', startTime, 'EndTime:', endTime);

    return (
        <div className="border border-[var(--border-gray)] rounded-2xl shadow-lg w-[800px] h-[380px] p-4 bg-[var(--inbetween)] rounded-2xl">
            <h3 
                className="text-[28px] font-montserrat font-bold tracking-[0.15em] text-[var(--text)] pl-10"
                style={{ 
                    position: 'relative',
                    transform: 'translateZ(0)', // Force hardware acceleration to prevent twitching
                    backfaceVisibility: 'hidden', // Prevent text flickering
                    WebkitFontSmoothing: 'antialiased', // Smooth font rendering
                }}
            >
                TYPING SPEED OVER TIME (WPM)
            </h3>
            <div className="mt-6">
                <ResponsiveContainer width="100%" height={260}>
                    <LineChart 
                        data={validatedSpeedLog}
                        style={{
                            transform: 'translateZ(0)', // Prevent twitching
                            backfaceVisibility: 'hidden',
                        }}
                    >
                        <CartesianGrid 
                            stroke={selectedTheme.border.replace('1)', '0.1)')} 
                            strokeDasharray="0" 
                        />
                        <XAxis
                            dataKey="time"
                            label={{
                                value: 'Time (seconds)',
                                position: 'insideBottom',
                                dy: 10,
                                fill: selectedTheme.text,
                                style: {
                                    textAnchor: 'middle',
                                    transform: 'translateZ(0)',
                                    backfaceVisibility: 'hidden',
                                }
                            }}
                            tick={{ 
                                fill: selectedTheme.text, 
                                fontSize: 12,
                                style: {
                                    transform: 'translateZ(0)',
                                    backfaceVisibility: 'hidden',
                                }
                            }}
                            ticks={ticks}
                            type="number"
                            domain={[1, duration]}
                        />
                        <YAxis
                            label={{
                                value: 'Speed (WPM)',
                                angle: -90,
                                position: 'insideLeft',
                                dy: 60,
                                fill: selectedTheme.text,
                                style: {
                                    textAnchor: 'middle',
                                    transform: 'translateZ(0)',
                                    backfaceVisibility: 'hidden',
                                }
                            }}
                            tick={{ 
                                fill: selectedTheme.text, 
                                fontSize: 12,
                                style: {
                                    transform: 'translateZ(0)',
                                    backfaceVisibility: 'hidden',
                                }
                            }}
                            domain={[0, 'auto']}
                        />
                        <Tooltip 
                            content={<CustomTooltip />}
                            animationDuration={0}
                            isAnimationActive={false}
                        />
                        <Line
                            type="monotone"
                            dataKey="rawWpm"
                            name="Raw WPM"
                            stroke={selectedTheme.caret}
                            strokeWidth={2}
                            dot={false}
                            isAnimationActive={false}
                            animationDuration={0}
                        />
                        <Line
                            type="monotone"
                            dataKey="accurateWpm"
                            name="Accurate WPM"
                            stroke={selectedTheme.correctChar}
                            strokeWidth={2}
                            dot={false}
                            isAnimationActive={false}
                            animationDuration={0}
                        />
                        {ticks.map((sec) => (
                            <ReferenceLine
                                key={`ref-line-${sec}`}
                                x={sec}
                                stroke={selectedTheme.borderGray}
                                strokeWidth={1}
                                strokeDasharray="3 3"
                            />
                        ))}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
};

export default SpeedChart;

