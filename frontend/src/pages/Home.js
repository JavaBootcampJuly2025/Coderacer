import React from 'react';
// Remove: import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import SpeedChart from '../components/SpeedChart';
import RightPanel from '../components/RightPanel';
import Footer from '../components/Footer';
import Leaderboard from '../components/Leaderboard';
import '../App.css';
import { useLevelContext } from '../context/LevelContext';

const Home = () => {
    const { latestSession } = useLevelContext();

    const handleClick = () => {
        document.activeElement.blur();
    };

    const speedLog = latestSession?.speedLog || [{ time: 0, cpm: 0 }, { time: 10, cpm: 50 }];
    const endTime = latestSession?.endTime || 10;

    return (
        <div className="home-wrapper min-h-screen bg-[#13223A] flex flex-col font-montserrat" onClick={handleClick}>
            <Header />
            <div className="main-body">
                <div className="flex-grow flex flex-col justify-between py-6">
                    <div className="flex justify-start items-start gap-6">
                        <SpeedChart endTime={endTime} speedLog={speedLog} />
                        <RightPanel />
                    </div>
                    <div className="mt-5">
                        <div className="flex justify-start gap-6">
                            {['easy', 'medium', 'hard'].map((type, index) => (
                                <Leaderboard key={index} type={type} />
                            ))}
                        </div>
                    </div>
                </div>
                <Footer />
            </div>
        </div>
    );
};

export default Home;