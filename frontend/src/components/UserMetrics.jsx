import React from 'react';
import { useTheme } from '../styles/ThemeContext';
import useAccountInfo from '../hooks/useAccountInfo';

const UserMetrics = () => {
    const { theme } = useTheme();
    const { avgCpm, avgAccuracy, loggedOn, username } = useAccountInfo();

    // Don't render if user is not logged in
    if (!loggedOn) {
        return (
            <div className="w-[320px] h-[300px] bg-[var(--inbetween)] border border-[var(--border-gray)] rounded-2xl shadow-lg flex items-center justify-center">
                <div className="text-center text-[var(--text)] opacity-70">
                    <div className="text-2xl mb-2">📊</div>
                    <div className="text-sm">Login to view metrics</div>
                </div>
            </div>
        );
    }

    return (
        <div className="w-[320px] h-[300px] bg-[var(--inbetween)] border border-[var(--border-gray)] rounded-2xl shadow-lg flex flex-col">
            <div className="bg-gradient-to-r from-[var(--accent)] to-[var(--primary-button)] p-4 rounded-t-2xl">
                <h3 className="text-base font-bold text-white text-center tracking-wide">
                    📊 YOUR STATS
                </h3>
            </div>

            <div className="p-6 flex-1 flex flex-col justify-center space-y-6">
                <div className="flex items-center justify-between p-4 bg-[var(--light)] rounded-lg border border-[var(--border-gray)]">
                    <div className="flex flex-col">
                        <span className="text-sm text-[var(--text)] opacity-80 font-medium">
                            AVG CPM
                        </span>
                        <span className="text-2xl font-bold text-[var(--accent)] font-mono">
                            {avgCpm ? avgCpm.toFixed(0) : '0'}
                        </span>
                    </div>
                    <div className="text-2xl">⚡</div>
                    </div>
                        <div className="flex items-center justify-between p-4 bg-[var(--light)] rounded-lg border border-[var(--border-gray)]">
                            <div className="flex flex-col">
                            <span className="text-sm text-[var(--text)] opacity-80 font-medium">
                                ACCURACY
                            </span>
                            <span className="text-2xl font-bold text-[var(--accent)] font-mono">
                                {avgAccuracy ? avgAccuracy.toFixed(1) : '0.0'}%
                            </span>
                            </div>
                        <div className="text-2xl">🎯</div>
                    </div>
                </div>
            </div>
    );
};

export default UserMetrics;