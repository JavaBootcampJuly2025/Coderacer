import './Home.css';
import React, { useEffect } from 'react';
import Chart from 'chart.js/auto';
import Leaderboard from './components/Leaderboard';
import Icon from './resources/icon.png'; // Import the icon

const Home = () => {
    useEffect(() => {
        const ctx = document.getElementById('lineChart')?.getContext('2d');
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

            return () => chart.destroy();
        }
    }, []);

    const handleClick = () => {
        document.activeElement.blur();
    };

    return (
            <div className="home-wrapper min-h-screen bg-[#13223A] flex flex-col font-montserrat" onClick={handleClick}>
                {/* Header */}
                <div className="w-full h-24 flex justify-between items-center px-5">
                    <div className="w-64 flex flex-row items-center space-x-3">
                        <img src={Icon} className="w-20 h-20" alt="Codegobrr Icon" />
                        <span className="text-[#24E5B7] text-4xl font-montserrat">codegobrr</span>
                    </div>
                    <div className="flex justify-center space-x-5">
                        {['Start Racing', 'Leaderboard', 'Profile'].map((label, index) => (
                            <button
                                key={index}
                                className="action w-24 h-12 bg-[#174065] rounded-full text-white hover:bg-[#1a4971] transition"
                            >
                                {label}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Main Content */}
                <div className="main-body">
                    <div className="flex-grow flex flex-col justify-between py-6">
                        <div className="flex justify-start items-start gap-6">
                            {/* Line Chart */}
                            <div className="w-[800px] h-[300px] p-4">
                                <canvas id="lineChart"></canvas>
                            </div>
                            {/* Right Panel */}
                            <div className="w-[450px] h-[260px] bg-black bg-opacity-22 flex flex-col">
                                <div className="w-full h-[46.25px] bg-[#174065]"></div>
                                <div className="flex-grow flex flex-col justify-center space-y-5 px-5">
                                    <div className="flex justify-center space-x-5">
                                        {['Challenges', 'Statistics', 'Settings'].map((label, index) => (
                                            <div
                                                key={index}
                                                className="w-24 h-12 bg-[#174065] rounded-full flex items-center justify-center text-white text-sm font-montserrat"
                                            >
                                                {label}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Leaderboard Sections */}
                        <div className="mt-5">
                            <div className="flex justify-start gap-6">
                                {['easy', 'medium', 'hard'].map((type, index) => (
                                    <Leaderboard key={index} type={type} />
                                ))}
                            </div>
                        </div>
                    </div>

                    {/* Footer */}
                    <footer style={{ marginTop: '2rem', fontSize: '0.9rem', color: '#ccc', textAlign: 'center' }}>
                        <p>© 2025 Coderacer Inc. All rights reserved.</p>
                    </footer>
                </div>
        </div>
    );
};

export default Home;