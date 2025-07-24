import React from 'react';
import {getRandomLevelWithDifficulty, getRandomProblemWithDifficulty} from '../services/apiService';
import { useNavigate } from 'react-router-dom';
import LevelSwitch from './ui/LevelSwitch';
import { useState } from 'react';

// Configuration constants
const PANEL_CONFIG = {
    panel: {
        width: '452px',
        height: '300px',
        borderRadius: '15px',
        background: 'var(--inbetween)',
        paddingX: '5',
    },
    buttons: {
        spacing: '5',
        default: {
            width: '410px',
            height: '16',
            textSize: '26px',
            fontWeight: 'bold',
            font: 'montserrat',
            background: 'var(--primary-button)',
            textColor: 'var(--text)',
            borderRadius: 'full',
            letterSpacing: '0.15em',
        },
    }
};

const RightPanel = () => {
    const [selectedDifficulty, setSelectedDifficulty] = useState('EASY');

    const navigate = useNavigate();

    const handlePlayClick = async () => {
        try {
            const levelData = await getRandomLevelWithDifficulty(selectedDifficulty);
            navigate("/level", { state: { level: levelData } });
        } catch (error) {
            console.error('Error loading level:', error);
        }
    };

    const handleThinkerClick = async () => {
        try {
            const problemData = await getRandomProblemWithDifficulty(selectedDifficulty);
            navigate("/gamemode", { state: { problem: problemData } });
        } catch (error) {
            console.error('Error loading level:', error);
        }
    };

    const buttons = [
        { label: 'THINKER MODE', onClick: handleThinkerClick },
        { label: 'SPEEDER MODE', onClick: handlePlayClick },
    ];

    return (
        <div
            className="w-[452px] h-[300px] rounded-2xl bg-[var(--inbetween)] flex flex-col"
            style={{
                boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
            }}
        >
            <LevelSwitch
                selectedDifficulty={selectedDifficulty}
                setSelectedDifficulty={setSelectedDifficulty}
            />
            <div className="flex-grow flex border-l border-r border-b border-[var(--border-gray)] rounded-b-2xl shadow-lg flex-col justify-center items-center space-y-5 px-5">
                <div className="flex flex-col justify-center items-center space-y-5 w-full">
                    {buttons.map(({ label, onClick }, index) => {
                        const buttonConfig = PANEL_CONFIG.buttons.default;
                        return (
                            <button
                                key={index}
                                className="w-[410px] h-20 bg-[var(--primary-button)] rounded-full flex items-center justify-center text-[var(--text)] font-montserrat font-bold cursor-pointer"
                                style={{
                                    fontSize: buttonConfig.textSize,
                                    letterSpacing: buttonConfig.letterSpacing,
                                    boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
                                }}
                                onClick={onClick}
                            >
                                {label}
                            </button>
                        );
                    })}
                </div>
            </div>
        </div>
    );
};

export default RightPanel;

