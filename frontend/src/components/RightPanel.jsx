// src/components/RightPanel.jsx
import React from 'react';
import { useNavigate } from 'react-router-dom';
import colors from '../styles/colors'; // Import colors

const RightPanel = () => {
    const navigate = useNavigate();

    const handlePlayClick = () => {
        navigate('/level?id=1');
    };

    return (
        <div className="w-[450px] h-[260px] bg-[var(--leaderboard-bg)] flex flex-col">
            <div className="w-full h-[46.25px] bg-[var(--primary-button)]"></div>
            <div className="flex-grow flex flex-col justify-center space-y-5 px-5">
                <div className="flex justify-center space-x-5">
                    {['Challenges', 'Statistics', 'Play'].map((label, index) => (
                        <div
                            key={index}
                            className="w-24 h-12 bg-[var(--primary-button)] rounded-full flex items-center justify-center text-[var(--text)] text-sm font-montserrat cursor-pointer"
                            onClick={label === 'Play' ? handlePlayClick : undefined}
                        >
                            {label}
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default RightPanel;