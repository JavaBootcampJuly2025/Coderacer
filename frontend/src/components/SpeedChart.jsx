import React from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, CartesianGrid, ResponsiveContainer } from 'recharts';

const SpeedChart = ({ endTime, speedLog }) => {
    if (!endTime || speedLog.length <= 1) return null;

    return (
        <div style={{ marginTop: '2rem' }}>
            <h3>Typing Speed Over Time</h3>
            <ResponsiveContainer width="100%" height={300}>
                <LineChart data={speedLog}>
                    <CartesianGrid stroke="#eee" strokeDasharray="5 5" />
                    <XAxis dataKey="time" label={{ value: 'Time (s)', position: 'insideBottom', dy: 10 }} />
                    <YAxis label={{ value: 'CPM', angle: -90, position: 'insideLeft' }} />
                    <Tooltip />
                    <Line type="monotone" dataKey="cpm" stroke="#8884d8" strokeWidth={2} dot={false} />
                </LineChart>
            </ResponsiveContainer>
        </div>
    );
};
export default SpeedChart;