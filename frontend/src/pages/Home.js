import React from 'react';
import Header from '../components/Header';
import SpeedChart from '../components/SpeedChart';
import RightPanel from '../components/RightPanel';
import Footer from '../components/Footer';
import Leaderboard from '../components/Leaderboard';
import '../App.css';
import { useLevelContext } from '../context/LevelContext';
import colors from '../styles/colors';
import { useTheme } from '../styles/ThemeContext';

const CONFIG = {
    root: {
        minHeight: 'min-h-screen',
        backgroundColor: 'bg-[#13223A]',
        flexDirection: 'flex-col',
        alignItems: 'items-center',
    },
    header: {
        width: 'w-full',
        height: 'h-[80px]',
        border: 'border border-[#59000000]',
    },
    mainBody: {
        width: 'w-full',
        maxWidth: 'max-w-7xl',
        flex: 'flex-grow',
        flexDirection: 'flex-col',
        justifyContent: 'justify-between',
        padding: 'py-6',
    },
    mainRow: {
        flexDirection: 'flex-row',
        justifyContent: 'justify-center',
        alignItems: 'items-start',
        gap: 'gap-6',
    },
    chartPanelCol: {
        flexDirection: 'flex-col',
        justifyContent: 'justify-center',
        alignItems: 'items-start',
        gap: 'gap-6',
    },
    buttonStatsRow: {
        flexDirection: 'flex-row',
        justifyContent: 'justify-center',
        alignItems: 'items-start',
        gap: 'gap-6',
    },
    speedChart: {
        width: 'w-5/5',
        height: 'h-[380px]',
        borderRadius: 'rounded-2xl',
    },
    rightPanel: {
        width: 'w-5/5',
        height: 'h-[300px]',
        borderRadius: 'rounded-2xl',
    },
    leaderboard: {
        width: 'w-[300px]',
        borderRadius: 'rounded-2xl',
    },
    footer: {
        width: 'w-full',
        height: 'h-[80px]',
        border: 'border border-[#59000000]',
    },
    typography: {
        fontFamily: 'font-montserrat',
        titleColor: 'text-[#24E5B7]',
        titleFontSize: 'text-2xl',
        titleFontWeight: 'font-medium',
    },
};

const Home = () => {
    const { latestSession } = useLevelContext();

    const handleClick = () => {
        document.activeElement.blur();
    };

    const speedLog = latestSession?.speedLog || [{ time: 0, rawCpm: 0, accurateWpm: 0 }, { time: 10, rawCpm: 50, accurateWpm: 40 }];
    const endTime = latestSession?.endTime || 10;
    const totalTyped = latestSession?.totalTyped || 0;
    const mistakes = latestSession?.mistakes || 0;
    const userInput = latestSession?.userInput || '';
    const codeSnippet = latestSession?.codeSnippet || '';

    return (
        <div
            className={`home-wrapper ${CONFIG.root.minHeight} ${CONFIG.root.backgroundColor} flex ${CONFIG.root.flexDirection} ${CONFIG.root.alignItems} ${CONFIG.typography.fontFamily}`}
            onClick={handleClick}
        >
            <div className={`${CONFIG.header.width} ${CONFIG.header.height} ${CONFIG.header.border}`}>
                <Header />
            </div>
            <div
                className={`main-body ${CONFIG.mainBody.width} ${CONFIG.mainBody.maxWidth} ${CONFIG.mainBody.flex} flex ${CONFIG.mainBody.flexDirection} ${CONFIG.mainBody.justifyContent} ${CONFIG.mainBody.padding}`}
            >
                <div className={`flex ${CONFIG.mainRow.flexDirection} ${CONFIG.mainRow.justifyContent} ${CONFIG.mainRow.alignItems} ${CONFIG.mainRow.gap}`}>
                    <div className={`${CONFIG.leaderboard.width} ${CONFIG.leaderboard.borderRadius}`}>
                        <Leaderboard />
                    </div>
                    <div className={`flex ${CONFIG.chartPanelCol.flexDirection} ${CONFIG.chartPanelCol.justifyContent} ${CONFIG.chartPanelCol.alignItems} ${CONFIG.chartPanelCol.gap}`}>
                        <div className={'flex ${CONFIG.chartPanelCol.flexDirection} ${CONFIG.chartPanelCol.justifyContent} ${CONFIG.chartPanelCol.alignItems} ${CONFIG.chartPanelCol.gap}'}>
                            <div className={`${CONFIG.rightPanel.width} ${CONFIG.rightPanel.height} ${CONFIG.rightPanel.borderRadius}`}>
                                <RightPanel />
                            </div>
                            <div>
                                HERE
                            </div>
                        </div>

                        <div className={`${CONFIG.speedChart.width} ${CONFIG.speedChart.height} ${CONFIG.speedChart.borderRadius}`}>
                            <SpeedChart
                                endTime={endTime}
                                speedLog={speedLog}
                                totalTyped={totalTyped}
                                mistakes={mistakes}
                                userInput={userInput}
                                codeSnippet={codeSnippet}
                            />
                        </div>
                    </div>
                </div>
            </div>
            <div className={`${CONFIG.footer.width} ${CONFIG.footer.height} ${CONFIG.footer.border}`}>
                <Footer />
            </div>
        </div>
    );
};

export default Home;