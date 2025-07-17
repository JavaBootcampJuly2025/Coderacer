import { useEffect, useRef } from 'react';
import Chart from 'chart.js/auto';
import { useTheme } from '../styles/ThemeContext';
import themes from '../styles/colors';

const useLineChart = (canvasId) => {
    const chartRef = useRef(null);
    const { theme } = useTheme(); // Access the current theme

    useEffect(() => {
        const ctx = document.getElementById(canvasId)?.getContext('2d');
        if (ctx) {
            const selectedTheme = themes[theme]; // Get colors for the current theme

            const chart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: [1, 2, 3, 4, 5, 6, 7],
                    datasets: [{
                        label: 'Progress',
                        data: [40, 70, 50, 80, 60, 90, 55],
                        borderColor: selectedTheme.accent, // Use --accent from theme
                        borderWidth: 2,
                        fill: true,
                        backgroundColor: selectedTheme.leaderboardBg, // Use --leaderboard-bg from theme
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
                            ticks: { color: selectedTheme.text }, // Use --text for ticks
                            grid: { display: false },
                        },
                        y: {
                            display: true,
                            ticks: { color: selectedTheme.text }, // Use --text for ticks
                            grid: { color: selectedTheme.border.replace('1)', '0.1)') }, // Use --border with reduced opacity
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
    }, [canvasId, theme]); // Add theme to dependencies to update chart on theme change

    return chartRef;
};

export default useLineChart;