import React from 'react';
import { useNavigate } from 'react-router-dom';

const RightPanel = () => {
    const navigate = useNavigate();

    const handlePlayClick = () => {
        navigate('/level?id=1');
    };

    return (
        <div className="w-[450px] h-[260px] bg-black bg-opacity-15 flex flex-col">
            <div className="w-full h-[46.25px] bg-[#174065]"></div>
            <div className="flex-grow flex flex-col justify-center space-y-5 px-5">
                <div className="flex justify-center space-x-5">
                    {['Challenges', 'Statistics', 'Play'].map((label, index) => (
                        <div
                            key={index}
                            className="w-24 h-12 bg-[#174065] rounded-full flex items-center justify-center text-white text-sm font-montserrat cursor-pointer"
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