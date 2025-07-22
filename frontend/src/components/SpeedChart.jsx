import React from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer, Area } from 'recharts';
import { useTheme } from '../styles/ThemeContext';
import themes from '../styles/colors';
import colors from '../styles/colors'; // Import colors

const SpeedChart = ({ endTime, speedLog }) => {
    const { theme } = useTheme();
    const selectedTheme = themes[theme];

    console.log('SpeedChart props:', { endTime, speedLog }); // Debug log
    if (!endTime || !speedLog || speedLog.length <= 1) return null;

    // Validate speedLog data
    const validatedSpeedLog = speedLog.map(entry => ({
        time: String(entry.time), // Ensure time is a string
        cpm: Number(entry.cpm) || 0, // Ensure cpm is a number
    }));

    return (
        <div className="w-[800px] h-[380px] p-4 bg-[var(--inbetween)] rounded-2xl">
            <h3 className="title">Typing Speed Over Time</h3>
            <ResponsiveContainer width="100%" height={260}>
                <LineChart data={speedLog}>
                    <CartesianGrid stroke={selectedTheme.border.replace('1)', '0.1)')} strokeDasharray="0" />
                    <XAxis dataKey="time" label={{ value: 'Time (s)', position: 'insideBottom', dy: 10 }} tick={{ fill: selectedTheme.text }} />
                    <YAxis label={{ value: 'CPM', angle: -90, position: 'insideLeft' }} tick={{ fill: selectedTheme.text }} />
                    <Tooltip />
                    <Area
                        type="monotone"
                        dataKey="cpm"
                        fill={selectedTheme.leaderboardBg}
                        stroke={selectedTheme.accent}
                        strokeWidth={2}
                        fillOpacity={1}
                        isAnimationActive={false}
                    />
                    <Line
                        type="monotone"
                        dataKey="cpm"
                        stroke={selectedTheme.accent}
                        strokeWidth={2}
                        dot={false}
                        isAnimationActive={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
};

export default SpeedChart;