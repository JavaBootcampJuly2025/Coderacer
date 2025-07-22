import React from 'react';
import '../App.css';
import colors from '../styles/colors'; // Import colors

const Leaderboard = () => {
    const titleConfig = {
        color: 'var(--accent)',
        font: 'montserrat',
        boldness: 'bold',
        size: '4xl'
    };

    const leaderboardData = [
        { rank: 1, name: 'Player1', score: 100 },
        { rank: 2, name: 'Player2', score: 85 },
        { rank: 3, name: 'Player3', score: 70 },
        { rank: 4, name: 'Player4', score: 60 },
        { rank: 5, name: 'Player5', score: 50 },
    ];

    return (
        <div className="w-[300px] h-[705px] bg-[var(--inbetween)] rounded-3xl flex flex-col">
            <div className="h-24 flex items-center justify-center">
                <span className={`text-[${titleConfig.color}] text-${titleConfig.size} font-${titleConfig.font} font-${titleConfig.boldness}`}>
                    Leaderboard
                </span>
            </div>
            <div className="flex-grow overflow-y-auto px-4">
                {leaderboardData.map((entry) => (
                    <div
                        key={entry.rank}
                        className="flex justify-between items-center py-2 border-b border-[var(--border-gray)]"
                    >
                        <span className="text-[var(--text)] text-sm font-montserrat">{entry.rank}. {entry.name}</span>
                        <span className="text-[var(--text)] text-sm font-montserrat">{entry.score}</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Leaderboard;