import { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';

const useLineChart = (canvasId) => {
    const chartRef = useRef(null);

    useEffect(() => {
        const ctx = document.getElementById(canvasId)?.getContext('2d');
        if (ctx) {
            const chart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: [1, 2, 3, 4, 5, 6, 7],
                    datasets: [{
                        label: 'Progress',
                        data: [40, 70, 50, 80, 60, 90, 55],
                        borderColor: '#FFC124',
                        borderWidth: 2,
                        fill: true,
                        backgroundColor: 'rgba(0, 0, 0, 0.29)',
                        tension: 0.4,
                        pointRadius: 0,
                    }],
                },
                options: {
                    plugins: {
                        legend: { display: false },
                    },
                    scales: {
                        x: {
                            display: true,
                            ticks: { color: '#ccc' },
                            grid: { display: false },
                        },
                        y: {
                            display: true,
                            ticks: { color: '#ccc' },
                            grid: { color: 'rgba(255, 255, 255, 0.1)' },
                        },
                    },
                    maintainAspectRatio: false,
                },
            });

            chartRef.current = chart;
        }

        return () => {
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        };
    }, [canvasId]);

    return chartRef;
};

export default useLineChart;