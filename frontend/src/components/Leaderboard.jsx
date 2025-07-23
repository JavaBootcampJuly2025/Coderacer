import React, {useState} from 'react';
import '../App.css';
import { useEffect } from 'react';
import {getTop} from "../services/apiService";
import colors from '../styles/colors';

const Leaderboard = () => {
    const [leaderboard, setLeaderboard] = useState([]);
    useEffect(() => {
        async function loadLeaderboard() {
            try {
                const response = await getTop();
                setLeaderboard(response);
            } catch (error) {

            }
        }

        loadLeaderboard();
    }, []);
    const titleConfig = {
        textSize: '26px',
        fontWeight: 'bold',
        font: 'montserrat',
        letterSpacing: '0.15em',
        textColor: 'var(--text)'
    };

    return (
        <div className="w-[300px] h-[705px] bg-[var(--inbetween)] rounded-3xl flex flex-col">
            <div className="h-24 flex items-center justify-center">
                <span className="text-[30px] font-montserrat font-bold tracking-[0.15em] text-[var(--text)]">
                    TOP 10
                </span>
            </div>
            <div className="flex-grow overflow-y-auto px-4">
                {leaderboard.map((entry, index) => (
                    <div
                        key={entry.id}
                        className="flex justify-between items-center py-2 border-b border-[var(--border-gray)]"
                    >
                        <span className="text-[var(--text)] text-sm font-montserrat">{index + 1}. {entry.username}</span>
                        <span className="text-[var(--text)] text-sm font-montserrat">{entry.matchmakingRating}</span>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default Leaderboard;