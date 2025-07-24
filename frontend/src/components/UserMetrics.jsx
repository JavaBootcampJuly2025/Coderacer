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
        <div className="w-[280px] h-[180px] bg-[var(--inbetween)] border-2 border-[var(--border-gray)] rounded-2xl shadow-lg overflow-hidden">
            {/* Header */}
            <div className="bg-gradient-to-r from-[var(--accent)] to-[var(--primary-button)] p-3">
                <h3 className="text-sm font-bold text-white text-center tracking-wide">
                    📊 YOUR STATS
                </h3>
            </div>

            {/* Content */}
            <div className="p-4 h-full flex flex-col justify-center">
                <div className="space-y-3">
                    {/* Average CPM */}
                    <div className="flex items-center justify-between p-2 bg-[var(--light)] rounded-lg border border-[var(--border-gray)]">
                        <div className="flex flex-col">
                            <span className="text-xs text-[var(--text)] opacity-70 font-medium">
                                AVG CPM
                            </span>
                            <span className="text-xl font-bold text-[var(--accent)] font-mono">
                                {avgCpm ? avgCpm.toFixed(0) : '0'}
                            </span>
                        </div>
                        <div className="text-lg">⚡</div>
                    </div>

                    {/* Average Accuracy */}
                    <div className="flex items-center justify-between p-2 bg-[var(--light)] rounded-lg border border-[var(--border-gray)]">
                        <div className="flex flex-col">
                            <span className="text-xs text-[var(--text)] opacity-70 font-medium">
                                ACCURACY
                            </span>
                            <span className="text-xl font-bold text-[var(--accent)] font-mono">
                                {avgAccuracy ? (avgAccuracy * 100).toFixed(1) : '0.0'}%
                            </span>
                        </div>
                        <div className="text-lg">🎯</div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserMetrics;