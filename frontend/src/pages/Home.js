import React from 'react';
import Header from '../components/Header';
import SpeedChart from '../components/SpeedChart';
import RightPanel from '../components/RightPanel';
import Footer from '../components/Footer';
import Leaderboard from '../components/Leaderboard';
import '../App.css';
import { useLevelContext } from '../context/LevelContext';
import colors from '../styles/colors'; // Import colors
import { useTheme } from '../styles/ThemeContext'


// Configuration object for layout, sizes, spacing, and styling
const CONFIG = {
    // Root container
    root: {
        minHeight: 'min-h-screen',
        backgroundColor: 'bg-[#13223A]',
        flexDirection: 'flex-col',
        alignItems: 'items-center',
    },
    // Header
    header: {
        width: 'w-full',
        height: 'h-[80px]',
        border: 'border border-[#59000000]',
    },
    // Main body
    mainBody: {
        width: 'w-full',
        maxWidth: 'max-w-7xl',
        flex: 'flex-grow',
        flexDirection: 'flex-col',
        justifyContent: 'justify-between',
        padding: 'py-6', // 24px
    },
    // SpeedChart and RightPanel row
    mainRow: {
        flexDirection: 'flex-row',
        justifyContent: 'justify-center',
        alignItems: 'items-start',
        gap: 'gap-6', // 24px
    },
    chartPanelCol: {
        flexDirection: 'flex-col',
        justifyContent: 'justify-center',
        alignItems: 'items-start',
        gap: 'gap-6', // 24px
    },
    // SpeedChart
    speedChart: {
        width: 'w-5/5', // 60% of container
        height: 'h-[380px]',
        // backgroundColor: 'bg-[var(--leaderboard-bg)]', // Semi-transparent
        // border: 'border-2 border-[#174065]',
        borderRadius: 'rounded-2xl', // 16px
    },
    // RightPanel
    rightPanel: {
        width: 'w-5/5', // 40% of container
        height: 'h-[300px]',
        // backgroundColor: 'bg-[var(--leaderboard-bg)]',
        borderRadius: 'rounded-2xl',
    },
    // Leaderboard
    leaderboard: {
        width: 'w-[300px]',
        borderRadius: 'rounded-2xl',
    },
    // Footer
    footer: {
        width: 'w-full',
        height: 'h-[80px]',
        border: 'border border-[#59000000]',
    },
    // Typography
    typography: {
        fontFamily: 'font-montserrat',
        titleColor: 'text-[#24E5B7]',
        titleFontSize: 'text-2xl', // 24px
        titleFontWeight: 'font-medium',
    },
};

const Home = () => {
    const { latestSession } = useLevelContext();

    const handleClick = () => {
        document.activeElement.blur();
    };

    const speedLog = latestSession?.speedLog || [{ time: 0, cpm: 0 }, { time: 10, cpm: 50 }];
    const endTime = latestSession?.endTime || 10;

    return (
        <div
            className={`home-wrapper ${CONFIG.root.minHeight} ${CONFIG.root.backgroundColor} flex ${CONFIG.root.flexDirection} ${CONFIG.root.alignItems} ${CONFIG.typography.fontFamily}`}
            onClick={handleClick}
        >
            {/* Header */}
            <div className={`${CONFIG.header.width} ${CONFIG.header.height} ${CONFIG.header.border}`}>
                <Header />
            </div>

            {/* Main Body */}
            <div
                className={`main-body ${CONFIG.mainBody.width} ${CONFIG.mainBody.maxWidth} ${CONFIG.mainBody.flex} flex ${CONFIG.mainBody.flexDirection} ${CONFIG.mainBody.justifyContent} ${CONFIG.mainBody.padding}`}
            >
                {/* Main Row */}
                <div className={`flex ${CONFIG.mainRow.flexDirection} ${CONFIG.mainRow.justifyContent} ${CONFIG.mainRow.alignItems} ${CONFIG.mainRow.gap}`}>
                    {/* Leaderboard */}
                    <div className={`${CONFIG.leaderboard.width} ${CONFIG.leaderboard.margin} ${CONFIG.leaderboard.backgroundColor} ${CONFIG.leaderboard.borderRadius}`}>
                        <Leaderboard />
                    </div>
                    {/* Chart and button column */}
                    <div className={`flex ${CONFIG.chartPanelCol.flexDirection} ${CONFIG.chartPanelCol.justifyContent} ${CONFIG.chartPanelCol.alignItems} ${CONFIG.chartPanelCol.gap}`}>
                        <div className={`${CONFIG.rightPanel.width} ${CONFIG.rightPanel.height} ${CONFIG.rightPanel.backgroundColor} ${CONFIG.rightPanel.borderRadius}`}>
                            <RightPanel />
                        </div>
                        <div className={`${CONFIG.speedChart.width} ${CONFIG.speedChart.height} ${CONFIG.speedChart.backgroundColor} ${CONFIG.speedChart.border} ${CONFIG.speedChart.borderRadius}`}>
                            <SpeedChart endTime={endTime} speedLog={speedLog} />
                        </div>

                    </div>
                </div>




            </div>

            {/* Footer */}
            <div className={`${CONFIG.footer.width} ${CONFIG.footer.height} ${CONFIG.footer.border}`}>
                <Footer />
            </div>
        </div>
    );
};

export default Home;