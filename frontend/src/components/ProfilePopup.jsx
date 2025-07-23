import React from 'react';
import NotLoggedInPopup from './NotLoggedInPopup';

const ProfilePopup = ({ username, rating, email, avgCpm, avgAccuracy, loggedOn, updateAccountInfo }) => {
    const logout = async (e) => {
        e.preventDefault();

        localStorage.removeItem('loginToken');
        localStorage.removeItem('loginId');

        updateAccountInfo();
    };

    if(loggedOn === false) return <NotLoggedInPopup />
    return (
        <div
            className="absolute rounded-2xl shadow-lg p-4 z-20 border break-words"
            style={{
                top: '6rem',
                right: '0.5rem',
                backgroundColor: 'var(--background)',
                color: 'var(--text)',
                borderColor: 'var(--accent)',
                width: '20rem',
            }}
        >
            <div
                className="font-semibold text-lg mb-3 border-b pb-2"
                style={{
                    borderColor: 'var(--accent)',
                    color: 'var(--accent)',
                }}
            >
                User Info
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Username:</span> {username || 'PlaceholderUser'}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Rating:</span> {rating || 0}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>Email:</span> {email || '<EMAIL>'}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>AvgCpm:</span> {avgCpm || 0}
            </div>
            <div className="mb-2">
                <span style={{ color: 'var(--label)' }}>AvgAccuracy:</span> {avgAccuracy || 0}
            </div>
            <button
                className="w-full py-3 rounded-xl bg-[var(--primary-button)] text-[var(--primary-button-text)] font-semibold hover:bg-[var(--primary-button-hover)] transition duration-300"
                onClick={logout}
            >
                Logout
            </button>
        </div>
    );
};

export default ProfilePopup;
