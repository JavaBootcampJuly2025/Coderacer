import React from 'react';
import Icon from '../assets/icon.png';
import Settings from '../assets/settings.png';

const Header = () => {
    return (
        <div className="w-full h-24 flex justify-between items-center px-5">
            <div className="w-64 flex flex-row items-center space-x-3">
                <img src={Icon} className="w-20 h-20" alt="Codegobrr Icon" />
                <span className="text-[#24E5B7] text-4xl font-montserrat">codegobrr</span>
            </div>
            <div className="flex justify-center space-x-5">
                <button
                    className="round-button w-12 h-12 bg-[#174065] rounded-full hover:bg-[#1a4971] transition flex items-center justify-center p-0"
                >
                    <img
                        src={Icon}
                        className="w-10 h-10 rounded-full object-cover"
                        alt="Profile"
                    />
                </button>
                <button
                    className="round-button w-12 h-12 bg-[#174065] rounded-full hover:bg-[#1a4971] transition flex items-center justify-center p-0"
                >
                    <img
                        src={Settings}
                        className="w-10 h-10 rounded-full object-cover"
                        alt="Settings"
                    />
                </button>
            </div>
        </div>
    );
};

export default Header;