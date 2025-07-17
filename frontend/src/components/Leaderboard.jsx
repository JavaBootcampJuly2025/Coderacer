import React from 'react';
import '../App.css';

const Leaderboard = ({ type }) => {
    // Hardcoded array of valid leaderboard types
    const leaderboardTypes = ['easy', 'medium', 'hard'];

    // Validate the type prop, default to 'easy' if invalid
    const selectedType = leaderboardTypes.includes(type) ? type : 'easy';

    // Placeholder data for the dynamic list
    const leaderboardData = [
        { rank: 1, name: 'Player1', score: 100 },
        { rank: 2, name: 'Player2', score: 85 },
        { rank: 3, name: 'Player3', score: 70 },
        { rank: 4, name: 'Player4', score: 60 },
        { rank: 5, name: 'Player5', score: 50 },
    ];

    return (
        <div className="w-[300px] h-[350px] bg-black bg-opacity-25 rounded-3xl flex flex-col">
            <div className="h-24 flex items-center justify-center">
        <span className="text-[#24E5B7] text-5xl font-montserrat font-light">
          {selectedType}
        </span>
            </div>
            <div className="flex-grow overflow-y-auto px-4">
                {leaderboardData.map((entry) => (
                    <div
                        key={entry.rank}
                        className="flex justify-between items-center py-2 border-b border-gray-700"
                    >
                        <span className="text-white text-sm font-montserrat">{entry.rank}. {entry.name}</span>
                        <span className="text-white text-sm font-montserrat">{entry.score}</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Leaderboard;