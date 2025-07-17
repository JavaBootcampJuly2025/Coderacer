import React from 'react';
import useLineChart from '../hooks/UseLineChart';

const ChartPanel = () => {
    const canvasId = 'lineChart';
    useLineChart(canvasId);

    return (
        <div className="w-[800px] h-[300px] p-4">
            <canvas id={canvasId}></canvas>
        </div>
    );
};

export default ChartPanel;