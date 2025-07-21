import React from 'react';
import { getRandomLevelWithParameters } from '../services/apiService';
import { useNavigate } from 'react-router-dom';
import LevelSwitch from './ui/LevelSwitch';
import LanguageGrid from './LanguageGrid';
import { useState } from 'react';

const RightPanel = () => {
    const [selectedLanguage, setSelectedLanguage] = useState(null);
    const [selectedDifficulty, setSelectedDifficulty] = useState('MEDIUM');

    const navigate = useNavigate();

    const handlePlayClick = async () => {
        try {
            console.log(selectedLanguage);
            const levelData = await getRandomLevelWithParameters(selectedLanguage, selectedDifficulty);
            navigate("/level", { state: { level: levelData } });
        } catch (error) {
            
        }
    };
    const handleChallengesClick = () => navigate('/challenges');
    const handleStatisticsClick = () => navigate('/statistics');

    return (
        <div className="w-[450px] h-[400px] rounded-[10px] bg-[var(--leaderboard-bg)] flex flex-col">
            <LevelSwitch 
                selectedDifficulty={selectedDifficulty}
                setSelectedDifficulty={setSelectedDifficulty}
            />
            <LanguageGrid
                selectedLanguage={selectedLanguage}
                setSelectedLanguage={setSelectedLanguage}
            />
            <div className="flex-grow flex flex-col justify-center space-y-5 px-5">
                <div className="flex justify-center space-x-5">
                    {['Challenges', 'Statistics', 'Play'].map((label, index) => (
                        <button
                            key={index}
                            className="w-24 h-12 bg-[var(--primary-button)] rounded-full flex items-center justify-center text-[var(--text)] text-sm font-montserrat cursor-pointer hover:bg-[var(--primary-button-hover)] transition-colors duration-200"
                            onClick={label === 'Play' ? handlePlayClick : label === 'Challenges' ? handleChallengesClick : handleStatisticsClick}
                        >
                            {label}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default RightPanel;